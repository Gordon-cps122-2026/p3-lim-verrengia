package library.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import library.model.LibraryDatabase;

/**
 * Top-level library window: frame setup, tabbed navigation between {@link BooksTab} and {@link
 * BorrowersTab}, and wiring for default-button behavior on the borrowers form.
 */
public class MainWindow {

  private final JFrame frame;
  private final BooksTab booksTab;
  private final BorrowersTab borrowersTab;

  /**
   * Builds the frame, tabs, loads list data from the given database, and prepares the window for
   * display.
   *
   * @param db library data source (use {@link LibraryDatabase#getInstance()} for the live app)
   */
  public MainWindow(LibraryDatabase db) {
    booksTab = new BooksTab(db);
    borrowersTab = new BorrowersTab(db);

    frame = new JFrame("Library App");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Books", booksTab);
    tabbedPane.addTab("Borrowers", borrowersTab.getPanel());

    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

    booksTab.refreshBookList();
    borrowersTab.refreshBorrowerList();

    borrowersTab.setRootPane(frame.getRootPane());

    frame.pack();
    frame.setMinimumSize(frame.getSize());
    frame.setLocationRelativeTo(null);
  }

  /**
   * Shows the main window.
   */
  public void display() {
    frame.setVisible(true);
  }

  /**
   * Updates the books list from the database CSV export.
   */
  public void refreshBookList() {
    booksTab.refreshBookList();
  }

  /**
   * Updates the borrowers list from the database CSV export.
   */
  public void refreshBorrowerList() {
    borrowersTab.refreshBorrowerList();
  }

  /**
   * Application entry point; builds {@link MainWindow} with the singleton database on the EDT.
   *
   * @param args unused
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(
        () -> {
          MainWindow main = new MainWindow(LibraryDatabase.getInstance());
          main.display();
        });
  }
}
