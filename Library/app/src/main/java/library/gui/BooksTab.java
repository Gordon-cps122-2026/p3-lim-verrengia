package library.gui;

import java.awt.*;
import javax.swing.*;
import library.model.LibraryDatabase;

/**
 * Books collection view and new-book form, using an internal {@link CardLayout} to switch between
 * the list card and the entry card.
 */
public class BooksTab extends JPanel {

  final CardLayout cardLayout;
  String currentCard;
  JPanel collectionCard;
  JPanel newBookCard;
  JTextArea bookListArea;
  JTextField titleText;
  JTextField authorText;
  JTextField callNumberText;

  private final LibraryDatabase db;

  /**
   * Builds the two cards and shows the collection view.
   *
   * @param db library data source
   */
  public BooksTab(LibraryDatabase db) {
    super();
    this.db = db;
    this.cardLayout = new CardLayout();
    setLayout(cardLayout);

    buildListArea();
    collectionCard = buildCollectionCard();
    newBookCard = buildNewBookCard();

    add(collectionCard, "collection");
    add(newBookCard, "newBook");

    currentCard = "collection";
    cardLayout.show(this, "collection");
  }

  private void buildListArea() {
    Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    bookListArea = new JTextArea();
    bookListArea.setFont(mono);
    bookListArea.setEditable(false);
  }

  private JPanel buildCollectionCard() {
    JPanel tab = new JPanel(new BorderLayout());

    tab.add(new JLabel("Collection"), BorderLayout.NORTH);

    JScrollPane bookScroll = new JScrollPane(bookListArea);
    tab.add(bookScroll, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    JButton addBook = new JButton("Add Book");
    addBook.addActionListener(e -> showCard("newBook"));
    south.add(addBook);
    tab.add(south, BorderLayout.SOUTH);

    return tab;
  }

  private JPanel buildNewBookCard() {
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
    back.addActionListener(e -> showCard("collection"));

    JButton add = new JButton("Add Book");
    applyPrimaryButtonStyle(add);
    add.addActionListener(
        e -> {
          String title = titleText.getText();
          String author = authorText.getText();
          String callNumber = callNumberText.getText();
          if (db.addBook(title, author, callNumber)) {
            if (!persistDatabaseSafely()) {
              return;
            }
            refreshBookList();
            clearAddBookFields();
            showCard("collection");
          } else {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(this),
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

  private void applyPrimaryButtonStyle(JButton button) {
    button.setBackground(Color.BLUE);
    button.setForeground(Color.WHITE);
    button.setOpaque(true);
    button.setBorderPainted(false);
  }

  /**
   * Shows the named card and keeps {@link #currentCard} in sync for tests and navigation.
   *
   * @param name {@code collection} or {@code newBook}
   */
  private void showCard(String name) {
    currentCard = name;
    cardLayout.show(this, name);
  }

  /**
   * Reloads the book list from the database CSV.
   */
  public void refreshBookList() {
    bookListArea.setText(db.getBookCsv());
  }

  private void clearAddBookFields() {
    callNumberText.setText("");
    titleText.setText("");
    authorText.setText("");
  }

  private boolean persistDatabaseSafely() {
    try {
      db.writeToFile();
      return true;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          SwingUtilities.getWindowAncestor(this),
          ex.getMessage() != null ? ex.getMessage() : ex.toString(),
          "Save failed",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }
}
