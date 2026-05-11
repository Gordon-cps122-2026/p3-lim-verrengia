package library.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import library.model.LibraryDatabase;

/**
 * Top-level library window: frame setup, tabbed navigation between {@link BooksTab} and {@link
 * BorrowersTab}, and wiring for default-button behavior on the borrowers form.
 */
public class MainWindow {

  private final JFrame frame;
  private final BooksTab booksTab;
  private final BorrowersTab borrowersTab;

  public MainWindow(LibraryDatabase db) {
    borrowersTab = new BorrowersTab(db);
    booksTab = new BooksTab(db);
    booksTab.setAfterCirculationSuccess(borrowersTab::refreshBorrowersAfterCirculation);
    borrowersTab.setRefreshBooksAfterCirculation(booksTab::refreshBookList);

    frame = new JFrame("Library App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().setBackground(LibraryUiTheme.BG_WHITE);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.setOpaque(true);
    tabbedPane.setBackground(LibraryUiTheme.BG_WHITE);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder());
    tabbedPane.addTab("Books", booksTab);
    tabbedPane.addTab("Borrowers", borrowersTab.getPanel());
    tabbedPane.setUI(new ZeroHeightTabBarUi());

    JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 32, 0));
    tabBar.setOpaque(true);
    tabBar.setBackground(LibraryUiTheme.TAB_BAR_BG);
    tabBar.setBorder(new EmptyBorder(0, 16, 0, 16));

    JLabel tabBooks = new JLabel("Books");
    JLabel tabBorrowers = new JLabel("Borrowers");
    tabBooks.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    tabBorrowers.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    Runnable syncStrip =
        () -> {
          int sel = tabbedPane.getSelectedIndex();
          styleTab(tabBooks, sel == 0);
          styleTab(tabBorrowers, sel == 1);
        };

    tabBooks.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            tabbedPane.setSelectedIndex(0);
          }
        });
    tabBorrowers.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            tabbedPane.setSelectedIndex(1);
          }
        });
    tabbedPane.addChangeListener(e -> syncStrip.run());

    tabBar.add(tabBooks);
    tabBar.add(tabBorrowers);

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(tabBar, BorderLayout.NORTH);
    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

    booksTab.refreshBookList();
    borrowersTab.refreshBorrowerList();

    borrowersTab.setRootPane(frame.getRootPane());

    syncStrip.run();

    int width = 960;
    int height = 720;
    frame.setSize(width, height);
    frame.setMinimumSize(new Dimension(width, height));
    frame.setLocationRelativeTo(null);
  }

  private static final Font TAB_FONT = new Font("SansSerif", Font.PLAIN, 14);
  private static final Color TAB_ACTIVE_COLOR = new Color(0x1A1A1A);
  private static final Color TAB_INACTIVE_COLOR = new Color(0x999999);

  private static void styleTab(JLabel label, boolean isActive) {
    label.setFont(
        isActive ? TAB_FONT.deriveFont(Font.BOLD) : TAB_FONT.deriveFont(Font.PLAIN));
    label.setForeground(isActive ? TAB_ACTIVE_COLOR : TAB_INACTIVE_COLOR);
    label.setBorder(
        BorderFactory.createCompoundBorder(
            isActive
                ? BorderFactory.createMatteBorder(0, 0, 2, 0, TAB_ACTIVE_COLOR)
                : BorderFactory.createEmptyBorder(0, 0, 2, 0),
            new EmptyBorder(10, 0, 10, 0)));
  }

  /** Hides the Swing tab row; navigation is the custom strip above. */
  private static final class ZeroHeightTabBarUi extends BasicTabbedPaneUI {
    @Override
    protected int calculateTabAreaHeight(int tabPlacement, int runIndex, int maxTabWidth) {
      return 0;
    }

    /** Tab row is hidden; painting it can leave clipped placeholder text below the custom strip. */
    @Override
    protected void paintTabArea(Graphics g, int tabPlacement, int selectedIndex) {}

    @Override
    protected void paintTab(
        Graphics g,
        int tabPlacement,
        Rectangle[] rects,
        int tabIndex,
        Rectangle iconRect,
        Rectangle textRect) {}

    @Override
    protected void paintFocusIndicator(
        Graphics g,
        int tabPlacement,
        Rectangle[] rects,
        int tabIndex,
        Rectangle iconRect,
        Rectangle textRect,
        boolean isSelected) {}

    @Override
    protected void paintContentBorderTopEdge(
        Graphics g, int tabPlacement, int selectedIndex, int x, int y, int w, int h) {}
  }

  public void display() {
    frame.setVisible(true);
    frame.toFront();
    frame.requestFocus();
    frame.setState(JFrame.NORMAL);
  }

  public void refreshBookList() {
    booksTab.refreshBookList();
  }

  public void refreshBorrowerList() {
    borrowersTab.refreshBorrowerList();
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          try {
            if (GraphicsEnvironment.isHeadless()) {
              System.err.println(
                  "Headless JVM — GUI cannot start. Run without -Djava.awt.headless=true.");
              return;
            }
            MainWindow main = new MainWindow(LibraryDatabase.getInstance());
            main.display();
          } catch (Throwable t) {
            t.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Could not start Library App:\n" + t.getMessage(),
                "Library App",
                JOptionPane.ERROR_MESSAGE);
          }
        });
  }
}
