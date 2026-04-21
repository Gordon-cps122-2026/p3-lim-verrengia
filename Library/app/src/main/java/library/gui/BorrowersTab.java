package library.gui;

import java.awt.*;
import javax.swing.*;
import library.model.LibraryDatabase;

/**
 * Borrowers list and new-borrower form with an internal {@link CardLayout}, result feedback, and
 * optional default-button wiring through {@link #setRootPane(JRootPane)}.
 */
public class BorrowersTab {

  final CardLayout cardLayout;
  String currentCard;
  JPanel borrowersCard;
  JPanel newBorrowerCard;
  JTextArea borrowerListArea;
  JTextField firstNameText;
  JTextField lastNameText;
  JTextField emailText;
  JTextField phoneText;
  JLabel resultLabel;
  JButton addBorrowerButton;

  private JRootPane rootPane;

  private final JPanel panel;
  private final LibraryDatabase db;

  /**
   * Builds the tab root panel and both cards.
   *
   * @param db library data source
   */
  public BorrowersTab(LibraryDatabase db) {
    this.db = db;
    this.cardLayout = new CardLayout();
    this.panel = new JPanel(this.cardLayout);

    buildListArea();
    borrowersCard = buildBorrowersCard();
    newBorrowerCard = buildNewBorrowerCard();

    panel.add(borrowersCard, "borrowers");
    panel.add(newBorrowerCard, "newBorrower");

    currentCard = "borrowers";
    cardLayout.show(panel, "borrowers");
  }

  /**
   * Returns the root panel managed by this tab (uses {@link CardLayout}).
   *
   * @return the borrowers tab root
   */
  public JPanel getPanel() {
    return panel;
  }

  private void buildListArea() {
    Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    borrowerListArea = new JTextArea();
    borrowerListArea.setFont(mono);
    borrowerListArea.setEditable(false);
  }

  private JPanel buildBorrowersCard() {
    JPanel tab = new JPanel(new BorderLayout());

    tab.add(new JLabel("Borrowers"), BorderLayout.NORTH);

    JScrollPane borrowerScroll = new JScrollPane(borrowerListArea);
    tab.add(borrowerScroll, BorderLayout.CENTER);

    JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
    JButton addBorrower = new JButton("Add Borrower");
    addBorrower.addActionListener(e -> showCard("newBorrower"));
    south.add(addBorrower);
    tab.add(south, BorderLayout.SOUTH);

    return tab;
  }

  private JPanel buildNewBorrowerCard() {
    JPanel card = new JPanel(new BorderLayout());
    // Direct child so tests that scan card.getComponents() can find this label.
    card.add(new JLabel("New Borrower"), BorderLayout.NORTH);

    resultLabel = new JLabel(" ");
    resultLabel.setForeground(new Color(0x20, 0x80, 0x20));

    GridBagLayout grid = new GridBagLayout();
    JPanel form = new JPanel(grid);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 6, 4, 6);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    form.add(resultLabel, gbc);

    firstNameText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 1, "First Name:", firstNameText);

    lastNameText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 2, "Last Name:", lastNameText);

    emailText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 3, "Email:", emailText);

    phoneText = new JTextField(24);
    addLabeledRow(form, grid, gbc, 4, "Phone:", phoneText);

    JPanel center = new JPanel(new BorderLayout());
    center.add(form, BorderLayout.CENTER);
    card.add(center, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    JButton back = new JButton("Back to Borrowers");
    back.addActionListener(e -> showCard("borrowers"));

    addBorrowerButton = new JButton("Add Borrower");
    applyPrimaryButtonStyle(addBorrowerButton);
    addBorrowerButton.addActionListener(
        e -> {
          String first = firstNameText.getText();
          String last = lastNameText.getText();
          String email = emailText.getText();
          String phone = phoneText.getText();
          if (db.addBorrower(first, last, email, phone)) {
            if (!persistDatabaseSafely()) {
              return;
            }
            resultLabel.setText(
                "Added borrower: " + first.trim() + " " + last.trim() + " " + email.trim());
            refreshBorrowerList();
            clearAddBorrowerFields();
            showCard("borrowers");
          } else {
            JOptionPane.showMessageDialog(
                SwingUtilities.getWindowAncestor(panel),
                "Borrower already exists or fields are empty",
                "Cannot add borrower",
                JOptionPane.WARNING_MESSAGE);
          }
        });

    bottom.add(back, BorderLayout.WEST);
    bottom.add(addBorrowerButton, BorderLayout.EAST);
    card.add(bottom, BorderLayout.SOUTH);

    return card;
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
   * Switches visible card and updates {@link #currentCard} and the optional default button.
   *
   * @param name {@code borrowers} or {@code newBorrower}
   */
  private void showCard(String name) {
    if ("newBorrower".equals(name)) {
      resultLabel.setText(" ");
    }
    currentCard = name;
    cardLayout.show(panel, name);
    syncDefaultButton();
  }

  /**
   * Attaches a root pane so the Add Borrower action can become the default button on the new
   * borrower card.
   *
   * @param rootPane frame root pane, or {@code null} to clear
   */
  public void setRootPane(JRootPane rootPane) {
    this.rootPane = rootPane;
    syncDefaultButton();
  }

  private void syncDefaultButton() {
    if (rootPane == null) {
      return;
    }
    if ("newBorrower".equals(currentCard)) {
      rootPane.setDefaultButton(addBorrowerButton);
    } else {
      rootPane.setDefaultButton(null);
    }
  }

  /**
   * Reloads the borrower list from the database CSV.
   */
  public void refreshBorrowerList() {
    borrowerListArea.setText(db.getBorrowerCsv());
  }

  private void clearAddBorrowerFields() {
    firstNameText.setText("");
    lastNameText.setText("");
    emailText.setText("");
    phoneText.setText("");
  }

  private boolean persistDatabaseSafely() {
    try {
      db.writeToFile();
      return true;
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(
          SwingUtilities.getWindowAncestor(panel),
          ex.getMessage() != null ? ex.getMessage() : ex.toString(),
          "Save failed",
          JOptionPane.ERROR_MESSAGE);
      return false;
    }
  }
}
