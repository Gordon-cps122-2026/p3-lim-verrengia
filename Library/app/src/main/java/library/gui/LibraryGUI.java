package library.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import library.model.LibraryDatabase;

/**
 * Swing front end for the library application. Uses a {@link CardLayout} to move between the home
 * view (books and borrowers) and forms for adding new records.
 */
public class LibraryGUI extends JFrame {

  private static final String CARD_HOME = "home";
  private static final String CARD_ADD_BOOK = "addBook";
  private static final String CARD_ADD_BORROWER = "addBorrower";

  private static final int TAB_BOOKS = 0;
  private static final int TAB_BORROWERS = 1;

  private final CardLayout cardLayout = new CardLayout();
  private final JTabbedPane homeTabs = new JTabbedPane();

  private JTextArea bookListArea;
  private JTextArea borrowerListArea;

  private JTextField callNumberText;
  private JTextField titleText;
  private JTextField authorText;

  private JTextField firstNameText;
  private JTextField lastNameText;
  private JTextField emailText;
  private JTextField phoneText;

  /**
   * Builds the frame, lays out all panels, wires listeners, and loads list data from the
   * singleton database.
   */
  public LibraryGUI() {
    super("Library App");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    buildListAreas();

    // CardLayout is installed on the content pane so the three primary views fill the frame.
    getContentPane().setLayout(cardLayout);
    getContentPane().add(createHomePanel(), CARD_HOME);
    getContentPane().add(createAddBookPanel(), CARD_ADD_BOOK);
    getContentPane().add(createAddBorrowerPanel(), CARD_ADD_BORROWER);

    refreshBookList();
    refreshBorrowerList();
    cardLayout.show(getContentPane(), CARD_HOME);

    pack();
    setMinimumSize(getSize());
    setLocationRelativeTo(null);
  }

  /**
   * Program entry point; schedules GUI construction on the Event Dispatch Thread.
   *
   * @param args unused command-line arguments
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(LibraryGUI::createAndShowGui);
  }

  /**
   * Instantiates {@link LibraryGUI} and makes it visible. Intended to be invoked on the EDT.
   */
  private static void createAndShowGui() {
    LibraryGUI frame = new LibraryGUI();
    frame.setVisible(true);
  }

  /**
   * Creates the shared {@link JTextArea} widgets used on the home screen lists.
   */
  private void buildListAreas() {
    Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    bookListArea = new JTextArea();
    bookListArea.setFont(mono);
    bookListArea.setEditable(false);

    borrowerListArea = new JTextArea();
    borrowerListArea.setFont(mono);
    borrowerListArea.setEditable(false);
  }

  /**
   * Builds the home card: a tabbed pane for books and borrowers plus navigation buttons.
   *
   * @return the fully configured home panel
   */
  private JPanel createHomePanel() {
    JPanel home = new JPanel(new BorderLayout());

    homeTabs.addTab("Books", createBooksTab());
    homeTabs.addTab("Borrowers", createBorrowersTab());

    home.add(homeTabs, BorderLayout.CENTER);
    return home;
  }

  /**
   * Builds the Books tab with header, scrollable list, and an Add Book control aligned to the
   * bottom-right.
   *
   * @return the Books tab component
   */
  private JPanel createBooksTab() {
    JPanel tab = new JPanel(new BorderLayout());

    tab.add(new JLabel("Collection"), BorderLayout.NORTH);

    JScrollPane bookScroll = new JScrollPane(bookListArea);
    tab.add(bookScroll, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    JButton addBook = new JButton("Add Book");
    // Navigate to the add-book form while keeping the Books tab selected for when we return.
    addBook.addActionListener(e -> cardLayout.show(getContentPane(), CARD_ADD_BOOK));
    south.add(addBook);
    tab.add(south, BorderLayout.SOUTH);

    return tab;
  }

  /**
   * Builds the Borrowers tab with header, scrollable list, and an Add Borrower control aligned to
   * the bottom-right.
   *
   * @return the Borrowers tab component
   */
  private JPanel createBorrowersTab() {
    JPanel tab = new JPanel(new BorderLayout());

    tab.add(new JLabel("Borrowers"), BorderLayout.NORTH);

    JScrollPane borrowerScroll = new JScrollPane(borrowerListArea);
    tab.add(borrowerScroll, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    JButton addBorrower = new JButton("Add Borrower");
    addBorrower.addActionListener(e -> cardLayout.show(getContentPane(), CARD_ADD_BORROWER));
    south.add(addBorrower);
    tab.add(south, BorderLayout.SOUTH);

    return tab;
  }

  /**
   * Builds the add-book form using {@link GridBagLayout} so labels stay right-aligned and fields
   * expand horizontally.
   *
   * @return the add-book card
   */
  private JPanel createAddBookPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    GridBagLayout grid = new GridBagLayout();
    JPanel form = new JPanel(grid);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 6, 4, 6);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    form.add(new JLabel("New Book"), gbc);

    callNumberText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 1, "Call Number:", callNumberText);

    titleText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 2, "Title:", titleText);

    authorText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 3, "Author:", authorText);

    panel.add(form, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    JButton back = new JButton("Back to Collection");
    back.addActionListener(
        e -> {
          showHomeSelectingBooksTab();
        });

    JButton add = new JButton("Add Book");
    applyPrimaryButtonStyle(add);
    add.addActionListener(
        e -> {
          String title = titleText.getText();
          String author = authorText.getText();
          String callNumber = callNumberText.getText();
          LibraryDatabase db = LibraryDatabase.getInstance();
          if (db.addBook(title, author, callNumber)) {
            if (!persistDatabaseSafely()) {
              // Stay on the form: the book is already in memory but not persisted to disk.
              return;
            }
            refreshBookList();
            clearAddBookFields();
            showHomeSelectingBooksTab();
          } else {
            JOptionPane.showMessageDialog(
                this,
                "Book already exists or fields are empty",
                "Cannot add book",
                JOptionPane.WARNING_MESSAGE);
          }
        });

    bottom.add(back, BorderLayout.WEST);
    bottom.add(add, BorderLayout.EAST);
    panel.add(bottom, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Builds the add-borrower form using {@link GridBagLayout} for consistent alignment with the
   * book form.
   *
   * @return the add-borrower card
   */
  private JPanel createAddBorrowerPanel() {
    JPanel panel = new JPanel(new BorderLayout());
    GridBagLayout grid = new GridBagLayout();
    JPanel form = new JPanel(grid);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 6, 4, 6);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    form.add(new JLabel("New Borrower"), gbc);

    firstNameText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 1, "First Name:", firstNameText);

    lastNameText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 2, "Last Name:", lastNameText);

    emailText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 3, "Email:", emailText);

    phoneText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 4, "Phone:", phoneText);

    panel.add(form, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    JButton back = new JButton("Back to Borrowers");
    back.addActionListener(e -> showHomeSelectingBorrowersTab());

    JButton add = new JButton("Add Borrower");
    applyPrimaryButtonStyle(add);
    add.addActionListener(
        e -> {
          String first = firstNameText.getText();
          String last = lastNameText.getText();
          String email = emailText.getText();
          String phone = phoneText.getText();
          LibraryDatabase db = LibraryDatabase.getInstance();
          if (db.addBorrower(first, last, email, phone)) {
            if (!persistDatabaseSafely()) {
              // Stay on the form: the borrower is already in memory but not persisted to disk.
              return;
            }
            refreshBorrowerList();
            clearAddBorrowerFields();
            showHomeSelectingBorrowersTab();
          } else {
            JOptionPane.showMessageDialog(
                this,
                "Borrower already exists or fields are empty",
                "Cannot add borrower",
                JOptionPane.WARNING_MESSAGE);
          }
        });

    bottom.add(back, BorderLayout.WEST);
    bottom.add(add, BorderLayout.EAST);
    panel.add(bottom, BorderLayout.SOUTH);

    return panel;
  }

  /**
   * Appends a label and field row to a {@link GridBagLayout} form. The label is right-aligned; the
   * field grows to fill remaining horizontal space.
   *
   * @param form parent form panel
   * @param grid layout shared with {@code form}
   * @param gbc constraints object reused across rows
   * @param row vertical position (1-based below the header)
   * @param labelText right-aligned caption text, including trailing colon where required
   * @param field text entry component for the row
   */
  private void addLabeledRow(
      JPanel form, GridBagLayout grid, GridBagConstraints gbc, int row, String labelText, JTextField field) {
    JLabel label = new JLabel(labelText, JLabel.RIGHT);

    gbc.gridy = row;
    gbc.gridwidth = 1;
    gbc.gridx = 0;
    gbc.anchor = GridBagConstraints.EAST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    grid.setConstraints(label, gbc);
    form.add(label);

    gbc.gridx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    grid.setConstraints(field, gbc);
    form.add(field);
  }

  /**
   * Applies the primary action styling (blue background, white text). {@code setOpaque(true)} and
   * {@code setBorderPainted(false)} help the custom colors show across common look-and-feels.
   *
   * @param button action button to style
   */
  private void applyPrimaryButtonStyle(JButton button) {
    button.setBackground(Color.BLUE);
    button.setForeground(Color.WHITE);
    button.setOpaque(true);
    button.setBorderPainted(false);
  }

  /**
   * Updates the books text area from the database CSV export.
   */
  public void refreshBookList() {
    LibraryDatabase db = LibraryDatabase.getInstance();
    bookListArea.setText(db.getBookCsv());
  }

  /**
   * Updates the borrowers text area from the database CSV export.
   */
  public void refreshBorrowerList() {
    LibraryDatabase db = LibraryDatabase.getInstance();
    borrowerListArea.setText(db.getBorrowerCsv());
  }

  /**
   * Shows the home card and selects the Books tab so returning navigation matches the button
   * label.
   */
  private void showHomeSelectingBooksTab() {
    cardLayout.show(getContentPane(), CARD_HOME);
    homeTabs.setSelectedIndex(TAB_BOOKS);
  }

  /**
   * Shows the home card and selects the Borrowers tab so returning navigation matches the button
   * label.
   */
  private void showHomeSelectingBorrowersTab() {
    cardLayout.show(getContentPane(), CARD_HOME);
    homeTabs.setSelectedIndex(TAB_BORROWERS);
  }

  /**
   * Persists the singleton {@link LibraryDatabase} to disk. Errors are surfaced to the user with a
   * modal dialog.
   *
   * @return {@code true} if the write succeeded; {@code false} if an error occurred
   */
  private boolean persistDatabaseSafely() {
    try {
      LibraryDatabase.getInstance().writeToFile();
      return true;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          this,
          ex.getMessage() != null ? ex.getMessage() : ex.toString(),
          "Save failed",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }

  /**
   * Clears all text fields on the add-book form after a successful add.
   */
  private void clearAddBookFields() {
    callNumberText.setText("");
    titleText.setText("");
    authorText.setText("");
  }

  /**
   * Clears all text fields on the add-borrower form after a successful add.
   */
  private void clearAddBorrowerFields() {
    firstNameText.setText("");
    lastNameText.setText("");
    emailText.setText("");
    phoneText.setText("");
  }
}
