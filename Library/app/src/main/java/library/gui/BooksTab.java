package library.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import library.model.Book;
import library.model.Borrower;
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
  private final List<BookRow> allRows = new ArrayList<>();
  private final BookTableModel tableModel = new BookTableModel();
  private final JTable bookTable = new JTable(tableModel);
  private final JTextField searchField = LibraryUiTheme.styledSearchField();
  private final InlineFeedback feedback = new InlineFeedback();
  private int hoverRow = -1;

  private static final String[] COLS = {"Title", "Author", "Call number", "Status", "Borrower"};
  private static final DateTimeFormatter DUE_FMT =
      DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH);

  static final class BookRow {
    final String mapKey;
    final Book book;

    BookRow(String mapKey, Book book) {
      this.mapKey = mapKey;
      this.book = book;
    }
  }

  private static final class BookTableModel extends AbstractTableModel {
    private final List<BookRow> rows = new ArrayList<>();

    void setRows(List<BookRow> next) {
      rows.clear();
      rows.addAll(next);
      fireTableDataChanged();
    }

    BookRow getRow(int modelIndex) {
      return rows.get(modelIndex);
    }

    @Override
    public int getRowCount() {
      return rows.size();
    }

    @Override
    public int getColumnCount() {
      return COLS.length;
    }

    @Override
    public String getColumnName(int column) {
      return COLS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      BookRow r = rows.get(rowIndex);
      Book b = r.book;
      switch (columnIndex) {
        case 0:
          return b.getTitle();
        case 1:
          return b.getAuthor();
        case 2:
          return b.getCallNumber();
        case 3:
          return b.isCheckedOut() ? "Unavailable" : "Available";
        case 4:
          if (!b.isCheckedOut()) {
            return "";
          }
          Borrower br = b.getCheckedOut().getBorrower();
          LocalDate due = b.getCheckedOut().getDueDate();
          String dueStr = due == null ? "" : "Due: " + DUE_FMT.format(due);
          return "<html><div>" + esc(br.getEmail()) + "</div><div style='color:#888888;font-size:11px'>"
              + esc(dueStr)
              + "</div></html>";
        default:
          return "";
      }
    }
  }

  private static String esc(String s) {
    if (s == null) {
      return "";
    }
    return s.replace("&", "&amp;").replace("<", "&lt;");
  }

  private static final class FlatLeftTableHeaderRenderer extends DefaultTableCellRenderer {
    FlatLeftTableHeaderRenderer() {
      setHorizontalAlignment(SwingConstants.LEFT);
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JLabel c =
          (JLabel)
              super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      c.setHorizontalAlignment(SwingConstants.LEFT);
      c.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
      c.setForeground(Color.BLACK);
      c.setBackground(LibraryUiTheme.TABLE_HEADER_BG);
      c.setOpaque(true);
      c.setBorder(
          BorderFactory.createCompoundBorder(
              BorderFactory.createMatteBorder(0, 0, 1, 0, LibraryUiTheme.BORDER_LIGHT),
              new EmptyBorder(8, 10, 8, 10)));
      return c;
    }
  }

  public BooksTab(LibraryDatabase db) {
    super();
    this.db = db;
    this.cardLayout = new CardLayout();
    setLayout(cardLayout);
    setBackground(LibraryUiTheme.BG_WHITE);

    buildListArea();
    collectionCard = buildCollectionCard();
    newBookCard = buildNewBookCard();

    add(collectionCard, "collection");
    add(newBookCard, "newBook");

    currentCard = "collection";
    cardLayout.show(this, "collection");
    refreshBookList();
  }

  private void buildListArea() {
    Font mono = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    bookListArea = new JTextArea();
    bookListArea.setFont(mono);
    bookListArea.setEditable(false);
  }

  private JPanel buildCollectionCard() {
    JPanel root = new JPanel(new BorderLayout(0, 12));
    root.setBackground(LibraryUiTheme.BG_WHITE);
    root.setBorder(new EmptyBorder(12, 16, 12, 16));

    JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
    searchWrap.setOpaque(false);
    searchField.putClientProperty("JTextField.placeholderText", "Search book by call number");
    searchField
        .getDocument()
        .addDocumentListener(
            new javax.swing.event.DocumentListener() {
              private void upd() {
                applySearchFilter();
              }

              @Override
              public void insertUpdate(javax.swing.event.DocumentEvent e) {
                upd();
              }

              @Override
              public void removeUpdate(javax.swing.event.DocumentEvent e) {
                upd();
              }

              @Override
              public void changedUpdate(javax.swing.event.DocumentEvent e) {
                upd();
              }
            });
    searchWrap.add(searchField, BorderLayout.CENTER);
    JLabel mag = new JLabel("\u2315");
    mag.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 18));
    mag.setForeground(LibraryUiTheme.TEXT_MUTED);
    searchWrap.add(mag, BorderLayout.EAST);
    root.add(searchWrap, BorderLayout.NORTH);

    bookTable.setRowHeight(LibraryUiTheme.TABLE_ROW_HEIGHT);
    bookTable.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
    bookTable.setGridColor(new Color(0xE5E5E5));
    bookTable.setShowGrid(true);
    bookTable.setShowVerticalLines(false);
    bookTable.setShowHorizontalLines(true);
    bookTable.setIntercellSpacing(new Dimension(0, 1));
    bookTable.setSelectionBackground(new Color(0xE8F0FE));
    bookTable.setSelectionForeground(LibraryUiTheme.PRIMARY_FILL);
    bookTable.setFillsViewportHeight(true);
    bookTable.setDefaultRenderer(Object.class, new BookCellRenderer());

    JTableHeader tableHeader = bookTable.getTableHeader();
    tableHeader.setReorderingAllowed(false);
    tableHeader.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
    tableHeader.setBackground(LibraryUiTheme.TABLE_HEADER_BG);
    tableHeader.setForeground(Color.BLACK);
    tableHeader.setOpaque(true);
    tableHeader.setDefaultRenderer(new FlatLeftTableHeaderRenderer());
    bookTable.addMouseMotionListener(
        new MouseAdapter() {
          @Override
          public void mouseMoved(MouseEvent e) {
            int r = bookTable.rowAtPoint(e.getPoint());
            if (r != hoverRow) {
              hoverRow = r;
              bookTable.repaint();
            }
          }

          @Override
          public void mouseExited(MouseEvent e) {
            hoverRow = -1;
            bookTable.repaint();
          }
        });

    JScrollPane sp = new JScrollPane(bookTable);
    sp.setBorder(BorderFactory.createLineBorder(LibraryUiTheme.BORDER_LIGHT, 1));
    sp.getViewport().setBackground(LibraryUiTheme.BG_WHITE);
    root.add(sp, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
    south.setOpaque(false);

    JButton ghostAddBook = new JButton("Add Book");
    ghostAddBook.addActionListener(e -> showCard("newBook"));
    ghostAddBook.setVisible(false);
    ghostAddBook.setMaximumSize(new Dimension(0, 0));
    ghostAddBook.setPreferredSize(new Dimension(0, 0));
    south.add(ghostAddBook);

    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    row.setOpaque(false);
    RoundedJButton checkout = new RoundedJButton("Checkout", true);
    checkout.addActionListener(e -> openCheckoutDialog());
    RoundedJButton ret = new RoundedJButton("Return", false);
    ret.addActionListener(e -> openReturnDialog());
    RoundedJButton renew = new RoundedJButton("Renew", false);
    renew.addActionListener(e -> doRenewFromSelection());
    RoundedJButton addUi = new RoundedJButton("Add book", false);
    addUi.addActionListener(e -> openAddBookDialog());
    row.add(checkout);
    row.add(ret);
    row.add(renew);
    row.add(addUi);
    row.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel row2 = new JPanel(new BorderLayout());
    row2.setOpaque(false);
    row2.add(row, BorderLayout.WEST);
    row2.add(feedback, BorderLayout.CENTER);
    row2.setAlignmentX(Component.LEFT_ALIGNMENT);

    south.add(row2);
    root.add(south, BorderLayout.SOUTH);

    return root;
  }

  private final class BookCellRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JLabel base =
          (JLabel)
              super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      base.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
      if (column == 4) {
        base.setText(value == null ? "" : value.toString());
      }
      Color bg =
          isSelected
              ? table.getSelectionBackground()
              : (row == hoverRow ? LibraryUiTheme.ROW_HOVER : LibraryUiTheme.BG_WHITE);
      if (column == 3 && value != null) {
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(bg);
        wrap.setOpaque(true);
        boolean avail = "Available".equals(value);
        StatusBadge badge = new StatusBadge(avail ? "Available" : "Unavailable", avail);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.fill = GridBagConstraints.NONE;
        wrap.add(badge, gc);
        return wrap;
      }
      base.setBackground(bg);
      base.setOpaque(true);
      base.setVerticalAlignment(SwingConstants.CENTER);
      return base;
    }
  }

  private void applySearchFilter() {
    String q = searchField.getText().trim().toLowerCase(Locale.ROOT);
    List<BookRow> filtered = new ArrayList<>();
    for (BookRow r : allRows) {
      if (q.isEmpty()
          || r.book.getCallNumber().toLowerCase(Locale.ROOT).contains(q)
          || r.mapKey.toLowerCase(Locale.ROOT).contains(q)) {
        filtered.add(r);
      }
    }
    tableModel.setRows(filtered);
  }

  private void rebuildAllRows() {
    allRows.clear();
    for (Map.Entry<String, Book> e : DatabaseView.bookEntries(db)) {
      allRows.add(new BookRow(e.getKey(), e.getValue()));
    }
    applySearchFilter();
  }

  private void openAddBookDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(this);
    JDialog d = new JDialog(w, "Add book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = dialogShell(d, "Add book");
    JTextField tTitle = LibraryUiTheme.styledTextField();
    JTextField tAuthor = LibraryUiTheme.styledTextField();
    JTextField tCall = LibraryUiTheme.styledTextField();
    JLabel dialogErr = new JLabel(" ");
    dialogErr.setForeground(LibraryUiTheme.ERROR);
    dialogErr.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 14, 0);
    gbc.gridy = 0;
    form.add(labeledField("Title", tTitle), gbc);
    gbc.gridy = 1;
    form.add(labeledField("Author", tAuthor), gbc);
    gbc.gridy = 2;
    form.add(labeledField("Call number", tCall), gbc);
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(dialogErr, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);

    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    JLabel cancel = dialogCancelLink(d);
    RoundedJButton add = new RoundedJButton("Add", true);
    add.addActionListener(
        ev -> {
          dialogErr.setText(" ");
          boolean ok =
              db.addBook(
                  tTitle.getText().trim(), tAuthor.getText().trim(), tCall.getText().trim());
          if (ok) {
            if (!persistDatabaseQuiet()) {
              d.dispose();
              return;
            }
            titleText.setText(tTitle.getText());
            authorText.setText(tAuthor.getText());
            callNumberText.setText(tCall.getText());
            refreshBookList();
            clearAddBookFields();
            feedback.showMessage("Book added \u2713", LibraryUiTheme.SUCCESS);
            d.dispose();
          } else {
            dialogErr.setText("Call number already exists.");
          }
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(add);
    footer.add(cancel, BorderLayout.WEST);
    footer.add(east, BorderLayout.EAST);
    root.add(footer, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private void openCheckoutDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(this);
    JDialog d = new JDialog(w, "Checkout book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = dialogShell(d, "Checkout book");
    JTextField emailF = LibraryUiTheme.styledTextField();
    JTextField callF = LibraryUiTheme.styledTextField();
    JLabel dialogErr = new JLabel(" ");
    dialogErr.setForeground(LibraryUiTheme.ERROR);
    dialogErr.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 14, 0);
    gbc.gridy = 0;
    form.add(labeledField("Borrower email", emailF), gbc);
    gbc.gridy = 1;
    form.add(labeledField("Book call number", callF), gbc);
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(dialogErr, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);
    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    JLabel cancel = dialogCancelLink(d);
    RoundedJButton go = new RoundedJButton("Checkout", true);
    go.addActionListener(
        ev -> {
          dialogErr.setText(" ");
          String email = emailF.getText().trim();
          String call = callF.getText().trim();
          String msg = CirculationHelper.tryCheckout(db, email, call);
          if ("ok".equals(msg)) {
            persistOrBail();
            feedback.showMessage("Checkout successful \u2713", LibraryUiTheme.SUCCESS);
            refreshBookList();
            d.dispose();
          } else {
            dialogErr.setText(msg);
          }
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(go);
    footer.add(cancel, BorderLayout.WEST);
    footer.add(east, BorderLayout.EAST);
    root.add(footer, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private void openReturnDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(this);
    JDialog d = new JDialog(w, "Return book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = dialogShell(d, "Return book");
    JTextField emailF = LibraryUiTheme.styledTextField();
    JTextField callF = LibraryUiTheme.styledTextField();
    JLabel dialogErr = new JLabel(" ");
    dialogErr.setForeground(LibraryUiTheme.ERROR);
    dialogErr.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 14, 0);
    gbc.gridy = 0;
    form.add(labeledField("Borrower email", emailF), gbc);
    gbc.gridy = 1;
    form.add(labeledField("Book call number", callF), gbc);
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(dialogErr, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);
    JPanel footer = new JPanel(new BorderLayout());
    footer.setOpaque(false);
    footer.add(dialogCancelLink(d), BorderLayout.WEST);
    RoundedJButton go = new RoundedJButton("Return", true);
    go.addActionListener(
        ev -> {
          dialogErr.setText(" ");
          boolean ok =
              CirculationHelper.tryReturn(db, emailF.getText().trim(), callF.getText().trim());
          if (ok) {
            persistOrBail();
            feedback.showMessage("Return successful \u2713", LibraryUiTheme.SUCCESS);
            refreshBookList();
            d.dispose();
          } else {
            dialogErr.setText("Book is not currently checked out.");
          }
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(go);
    footer.add(east, BorderLayout.EAST);
    root.add(footer, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private void doRenewFromSelection() {
    feedback.clearNow();
    int r = bookTable.getSelectedRow();
    if (r < 0) {
      feedback.showMessage("Failed to renew.", LibraryUiTheme.ERROR);
      return;
    }
    BookRow row = tableModel.getRow(r);
    String key = row.mapKey;
    Book b = row.book;
    if (b == null || !b.isCheckedOut()) {
      feedback.showMessage("Failed to renew.", LibraryUiTheme.ERROR);
      return;
    }
    if (b.getCheckedOut().isRenewed()) {
      feedback.showMessage("Due date can be renewed only once.", LibraryUiTheme.ERROR);
      return;
    }
    if (db.renew(key)) {
      persistOrBail();
      feedback.showMessage("Renew successful", LibraryUiTheme.SUCCESS);
      refreshBookList();
    } else {
      feedback.showMessage("Failed to renew.", LibraryUiTheme.ERROR);
    }
  }

  private JPanel labeledField(String label, JTextField field) {
    JPanel p = new JPanel(new BorderLayout(0, 6));
    p.setOpaque(false);
    JLabel l = new JLabel(label);
    l.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 12));
    l.setForeground(new Color(0x555555));
    p.add(l, BorderLayout.NORTH);
    p.add(field, BorderLayout.CENTER);
    return p;
  }

  private JPanel dialogShell(JDialog d, String title) {
    JPanel root = new JPanel(new BorderLayout(0, 16));
    root.setBackground(LibraryUiTheme.BG_WHITE);
    root.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LibraryUiTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 24, 20, 24)));
    JLabel head = new JLabel(title);
    head.setFont(LibraryUiTheme.uiFont(Font.BOLD, 16));
    head.setForeground(LibraryUiTheme.PRIMARY_FILL);
    root.add(head, BorderLayout.NORTH);
    d.setContentPane(root);
    return root;
  }

  private JLabel dialogCancelLink(JDialog d) {
    JLabel cancel = new JLabel("Cancel");
    cancel.setForeground(new Color(0x555555));
    cancel.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
    cancel.setBorder(new EmptyBorder(10, 0, 10, 16));
    cancel.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
    cancel.addMouseListener(
        new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
            d.dispose();
          }
        });
    return cancel;
  }

  private JPanel buildNewBookCard() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(LibraryUiTheme.BG_WHITE);
    GridBagLayout grid = new GridBagLayout();
    JPanel form = new JPanel(grid);
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 6, 4, 6);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
    JLabel nb = new JLabel("New Book");
    nb.setFont(LibraryUiTheme.uiFont(Font.BOLD, 16));
    form.add(nb, gbc);

    callNumberText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 1, "Call Number:", callNumberText);

    titleText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 2, "Title:", titleText);

    authorText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 3, "Author:", authorText);

    panel.add(form, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setOpaque(false);
    RoundedJButton back = new RoundedJButton("Back to Collection", false);
    back.addActionListener(e -> showCard("collection"));

    RoundedJButton add = new RoundedJButton("Add Book", true);
    add.addActionListener(
        e -> {
          String title = titleText.getText();
          String author = authorText.getText();
          String callNumber = callNumberText.getText();
          if (db.addBook(title, author, callNumber)) {
            if (!persistDatabaseQuiet()) {
              return;
            }
            refreshBookList();
            clearAddBookFields();
            showCard("collection");
          } else {
            feedback.showMessage("Call number already exists.", LibraryUiTheme.ERROR);
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
    label.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));

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

  private void showCard(String name) {
    feedback.clearNow();
    currentCard = name;
    cardLayout.show(this, name);
  }

  public void refreshBookList() {
    rebuildAllRows();
    bookListArea.setText(db.getBookCsv());
  }

  private void clearAddBookFields() {
    callNumberText.setText("");
    titleText.setText("");
    authorText.setText("");
  }

  /** @return false if persist failed */
  private boolean persistDatabaseQuiet() {
    try {
      db.writeToFile();
      return true;
    } catch (Exception ex) {
      feedback.showMessage(
          ex.getMessage() != null ? ex.getMessage() : ex.toString(), LibraryUiTheme.ERROR);
      return false;
    }
  }

  private void persistOrBail() {
    persistDatabaseQuiet();
  }
}
