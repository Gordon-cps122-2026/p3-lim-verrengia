package library.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import library.model.Book;
import library.model.Borrower;
import library.model.CheckedOut;
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
  private final JPanel borrowerRowsPanel = new JPanel();
  private final JTextField searchBorrowerField = LibraryUiTheme.styledSearchField();
  private final InlineFeedback feedback = new InlineFeedback();
  private String expandedEmail = null;

  private static final DateTimeFormatter DUE_SHORT = DateTimeFormatter.ofPattern("MM/dd");

  public BorrowersTab(LibraryDatabase db) {
    this.db = db;
    this.cardLayout = new CardLayout();
    this.panel = new JPanel(this.cardLayout);
    panel.setBackground(LibraryUiTheme.BG_WHITE);

    buildListArea();
    borrowersCard = buildBorrowersCard();
    newBorrowerCard = buildNewBorrowerCard();

    panel.add(borrowersCard, "borrowers");
    panel.add(newBorrowerCard, "newBorrower");

    currentCard = "borrowers";
    cardLayout.show(panel, "borrowers");
  }

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
    JPanel tab = new JPanel(new BorderLayout(0, 12));
    tab.setBackground(LibraryUiTheme.BG_WHITE);
    tab.setBorder(new EmptyBorder(12, 16, 12, 16));

    JPanel body = new JPanel(new BorderLayout(0, 12));
    body.setOpaque(false);

    JPanel searchWrap = new JPanel(new BorderLayout(8, 0));
    searchWrap.setOpaque(false);
    searchBorrowerField.putClientProperty("JTextField.placeholderText", "Search borrower by email");
    searchBorrowerField
        .getDocument()
        .addDocumentListener(
            new javax.swing.event.DocumentListener() {
              @Override
              public void insertUpdate(javax.swing.event.DocumentEvent e) {
                applyBorrowerFilter();
              }

              @Override
              public void removeUpdate(javax.swing.event.DocumentEvent e) {
                applyBorrowerFilter();
              }

              @Override
              public void changedUpdate(javax.swing.event.DocumentEvent e) {
                applyBorrowerFilter();
              }
            });
    searchWrap.add(searchBorrowerField, BorderLayout.CENTER);
    JLabel mag = new JLabel("\u2315");
    mag.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 18));
    mag.setForeground(LibraryUiTheme.TEXT_MUTED);
    searchWrap.add(mag, BorderLayout.EAST);
    body.add(searchWrap, BorderLayout.NORTH);

    borrowerRowsPanel.setLayout(new BoxLayout(borrowerRowsPanel, BoxLayout.Y_AXIS));
    borrowerRowsPanel.setBackground(LibraryUiTheme.BG_WHITE);
    JScrollPane sp = new JScrollPane(borrowerRowsPanel);
    sp.setBorder(BorderFactory.createLineBorder(LibraryUiTheme.BORDER_LIGHT, 1));
    sp.getViewport().setBackground(LibraryUiTheme.BG_WHITE);
    body.add(sp, BorderLayout.CENTER);
    tab.add(body, BorderLayout.CENTER);

    JPanel south = new JPanel();
    south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
    south.setOpaque(false);

    JButton ghostAdd = new JButton("Add Borrower");
    ghostAdd.addActionListener(e -> showCard("newBorrower"));
    ghostAdd.setVisible(false);
    ghostAdd.setMaximumSize(new Dimension(0, 0));
    ghostAdd.setPreferredSize(new Dimension(0, 0));
    south.add(ghostAdd);

    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    row.setOpaque(false);
    RoundedJButton checkout = new RoundedJButton("Checkout", true);
    checkout.addActionListener(e -> openCheckoutDialog());
    RoundedJButton ret = new RoundedJButton("Return", false);
    ret.addActionListener(e -> openReturnDialog());
    RoundedJButton renew = new RoundedJButton("Renew", false);
    renew.addActionListener(e -> openRenewDialog());
    RoundedJButton addUi = new RoundedJButton("Add borrower", false);
    addUi.addActionListener(e -> showCard("newBorrower"));
    row.add(checkout);
    row.add(ret);
    row.add(renew);
    row.add(addUi);

    JPanel row2 = new JPanel(new BorderLayout());
    row2.setOpaque(false);
    row2.add(row, BorderLayout.WEST);
    row2.add(feedback, BorderLayout.CENTER);

    south.add(row2);
    tab.add(south, BorderLayout.SOUTH);

    return tab;
  }

  private void applyBorrowerFilter() {
    rebuildBorrowerRowsUi();
  }

  private void rebuildBorrowerRowsUi() {
    borrowerRowsPanel.removeAll();
    String q = searchBorrowerField.getText().trim().toLowerCase(Locale.ROOT);
    List<Map.Entry<String, Borrower>> filtered = new java.util.ArrayList<>();
    for (Map.Entry<String, Borrower> e : DatabaseView.borrowerEntries(db)) {
      if (!q.isEmpty() && !e.getKey().toLowerCase(Locale.ROOT).contains(q)) {
        continue;
      }
      filtered.add(e);
    }
    for (int i = 0; i < filtered.size(); i++) {
      borrowerRowsPanel.add(new BorrowerRowHost(filtered.get(i).getValue()));
      if (i < filtered.size() - 1) {
        borrowerRowsPanel.add(separator());
      }
    }
    borrowerRowsPanel.add(Box.createVerticalGlue());
    borrowerRowsPanel.revalidate();
    borrowerRowsPanel.repaint();
  }

  private static JPanel separator() {
    JPanel p = new JPanel();
    p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
    p.setMinimumSize(new Dimension(1, 1));
    p.setPreferredSize(new Dimension(0, 1));
    p.setAlignmentX(Component.LEFT_ALIGNMENT);
    p.setBackground(LibraryUiTheme.ROW_DIVIDER);
    return p;
  }

  /** Mouse listeners on the header row and its descendants only (not the expand panel). */
  private static void wireHeaderSubtreeMouse(Component root, MouseAdapter adapter) {
    root.addMouseListener(adapter);
    if (root instanceof java.awt.Container) {
      for (Component ch : ((java.awt.Container) root).getComponents()) {
        wireHeaderSubtreeMouse(ch, adapter);
      }
    }
  }

  private void syncBorrowerRowExpansionUi() {
    for (Component c : borrowerRowsPanel.getComponents()) {
      if (c instanceof BorrowerRowHost h) {
        h.applySharedExpansionState();
      }
    }
    borrowerRowsPanel.revalidate();
    borrowerRowsPanel.repaint();
  }

  private final class BorrowerRowHost extends JPanel {
    private static final int ROW_H = 52;

    private final Borrower borrower;
    /** Fixed 52px-high row: GridLayout 1×3 with left=name+chevron, center=email, right=phone. */
    private final JPanel header;

    private final JPanel expand;
    /** Chevron ▼ / ▲; only its text updates when expanded — header structure is unchanged. */
    private final JLabel chevronLabel = new JLabel("\u25BC");

    BorrowerRowHost(Borrower borrower) {
      super();
      this.borrower = borrower;
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      setOpaque(false);
      setAlignmentX(Component.LEFT_ALIGNMENT);
      setBorder(new EmptyBorder(4, 0, 4, 0));

      String fn = borrower.getFirstName() != null ? borrower.getFirstName() : "";
      String ln = borrower.getLastName() != null ? borrower.getLastName() : "";
      String displayName = (fn + " " + ln).trim();

      JPanel leftCol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
      JPanel centerCol = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
      JPanel rightCol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
      leftCol.setOpaque(false);
      centerCol.setOpaque(false);
      rightCol.setOpaque(false);

      chevronLabel.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 12));
      chevronLabel.setForeground(LibraryUiTheme.TEXT_MUTED);
      JLabel nameLabel = new JLabel(displayName.isEmpty() ? "\u2014" : displayName);
      nameLabel.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
      JLabel emailLabel =
          new JLabel(borrower.getEmail() != null ? borrower.getEmail() : "");
      emailLabel.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));
      JLabel phoneLabel =
          new JLabel(borrower.getPhone() != null ? borrower.getPhone() : "");
      phoneLabel.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 14));

      leftCol.add(chevronLabel);
      leftCol.add(nameLabel);
      centerCol.add(emailLabel);
      rightCol.add(phoneLabel);

      JPanel headerRow =
          new JPanel(new GridLayout(1, 3, 0, 0)) {
            @Override
            public Dimension getPreferredSize() {
              Dimension d = super.getPreferredSize();
              return new Dimension(d.width, ROW_H);
            }

            @Override
            public Dimension getMaximumSize() {
              return new Dimension(Integer.MAX_VALUE, ROW_H);
            }

            @Override
            public Dimension getMinimumSize() {
              return new Dimension(1, ROW_H);
            }
          };
      headerRow.setOpaque(true);
      headerRow.setBackground(LibraryUiTheme.BG_WHITE);
      headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
      // Height fixed at 52; width follows parent (avoid getWidth()==0 during construction).
      headerRow.add(leftCol); // FIRST — column 1
      headerRow.add(centerCol); // SECOND — column 2
      headerRow.add(rightCol); // THIRD — column 3

      header = headerRow;

      expand = buildExpandPanel();

      MouseAdapter rowMouse =
          new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              toggleExpand();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
              if (!Objects.equals(borrower.getEmail(), expandedEmail)) {
                setHeaderRowBackground(LibraryUiTheme.ROW_HOVER);
              }
            }

            @Override
            public void mouseExited(MouseEvent e) {
              // Moving between header children fires exit on the child; only clear when pointer left header.
              if (GraphicsEnvironment.isHeadless()) {
                updateHeaderBg();
                return;
              }
              PointerInfo pi = MouseInfo.getPointerInfo();
              if (pi == null) {
                updateHeaderBg();
                return;
              }
              Point rel = pi.getLocation();
              SwingUtilities.convertPointFromScreen(rel, header);
              if (!header.contains(rel)) {
                updateHeaderBg();
              }
            }
          };
      wireHeaderSubtreeMouse(header, rowMouse);

      add(header);
      add(expand);
      expand.setVisible(Objects.equals(borrower.getEmail(), expandedEmail));
      updateChevron();
      updateHeaderBg();
    }

    private void applySharedExpansionState() {
      expand.setVisible(Objects.equals(borrower.getEmail(), expandedEmail));
      updateChevron();
      updateHeaderBg();
    }

    @Override
    public Dimension getPreferredSize() {
      int h = ROW_H + (expand.isVisible() ? expand.getPreferredSize().height : 0);
      Dimension d = super.getPreferredSize();
      return new Dimension(d.width, h);
    }

    @Override
    public Dimension getMaximumSize() {
      return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }

    private void toggleExpand() {
      if (Objects.equals(borrower.getEmail(), expandedEmail)) {
        expandedEmail = null;
      } else {
        expandedEmail = borrower.getEmail();
      }
      BorrowersTab.this.syncBorrowerRowExpansionUi();
    }

    private void updateChevron() {
      boolean on = Objects.equals(borrower.getEmail(), expandedEmail);
      chevronLabel.setText(on ? "\u25B2" : "\u25BC");
    }

    private void setHeaderRowBackground(Color c) {
      header.setBackground(c);
      header.repaint();
    }

    private void updateHeaderBg() {
      boolean expanded = Objects.equals(borrower.getEmail(), expandedEmail);
      Color c = expanded ? LibraryUiTheme.EXPAND_ROW : LibraryUiTheme.BG_WHITE;
      setHeaderRowBackground(c);
    }

    private JPanel buildExpandPanel() {
      JPanel wrap = new JPanel(new BorderLayout(8, 8));
      wrap.setOpaque(true);
      wrap.setBackground(LibraryUiTheme.BORROWER_EXPAND_PANEL_BG);
      wrap.setBorder(new EmptyBorder(8, 28, 12, 12));
      Collection<Book> books = borrower.getAllBooks();
      int n = books.size();
      JLabel title = new JLabel(n + " borrowed book(s)");
      title.setFont(LibraryUiTheme.uiFont(Font.BOLD, 13));
      title.setForeground(LibraryUiTheme.PRIMARY_FILL);
      wrap.add(title, BorderLayout.NORTH);
      JPanel lines = new JPanel();
      lines.setLayout(new BoxLayout(lines, BoxLayout.Y_AXIS));
      lines.setOpaque(false);
      for (Book b : books) {
        CheckedOut co = borrower.getCheckedOut(b.getKey());
        if (co == null) {
          co = borrower.getCheckedOut(b.getCallNumber());
        }
        String due =
            co != null && co.getDueDate() != null ? DUE_SHORT.format(co.getDueDate()) : "";
        JPanel line = new JPanel(new BorderLayout());
        line.setOpaque(false);
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel tl = new JLabel(b.getTitle());
        tl.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 13));
        JLabel dr = new JLabel("Due: " + due);
        dr.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 12));
        dr.setForeground(LibraryUiTheme.TEXT_SUB);
        line.add(tl, BorderLayout.WEST);
        line.add(dr, BorderLayout.EAST);
        lines.add(line);
      }
      wrap.add(lines, BorderLayout.CENTER);
      return wrap;
    }
  }

  private JPanel buildNewBorrowerCard() {
    JPanel card = new JPanel(new BorderLayout());
    card.setBackground(LibraryUiTheme.BG_WHITE);
    card.add(new JLabel("New Borrower"), BorderLayout.NORTH);

    resultLabel = new JLabel(" ");
    resultLabel.setForeground(LibraryUiTheme.SUCCESS);

    GridBagLayout grid = new GridBagLayout();
    JPanel form = new JPanel(grid);
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 6, 4, 6);

    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    form.add(resultLabel, gbc);

    firstNameText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 1, "First Name:", firstNameText);

    lastNameText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 2, "Last Name:", lastNameText);

    emailText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 3, "Email:", emailText);

    phoneText = LibraryUiTheme.styledTextField();
    addLabeledRow(form, grid, gbc, 4, "Phone:", phoneText);

    JPanel center = new JPanel(new BorderLayout());
    center.setOpaque(false);
    center.add(form, BorderLayout.CENTER);
    card.add(center, BorderLayout.CENTER);

    JPanel bottom = new JPanel(new BorderLayout());
    bottom.setOpaque(false);
    RoundedJButton back = new RoundedJButton("Back to Borrowers", false);
    back.addActionListener(e -> showCard("borrowers"));

    addBorrowerButton = new RoundedJButton("Add Borrower", true);
    addBorrowerButton.addActionListener(
        e -> {
          String first = firstNameText.getText();
          String last = lastNameText.getText();
          String email = emailText.getText();
          String phone = phoneText.getText();
          if (db.addBorrower(first, last, email, phone)) {
            if (!persistDatabaseQuiet()) {
              return;
            }
            resultLabel.setForeground(LibraryUiTheme.SUCCESS);
            resultLabel.setText(
                "Added borrower: " + first.trim() + " " + last.trim() + " " + email.trim());
            refreshBorrowerList();
            clearAddBorrowerFields();
            showCard("borrowers");
          } else {
            resultLabel.setForeground(LibraryUiTheme.ERROR);
            resultLabel.setText("Borrower already exists or fields are empty.");
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
    if ("newBorrower".equals(name)) {
      resultLabel.setText(" ");
      resultLabel.setForeground(LibraryUiTheme.SUCCESS);
    }
    currentCard = name;
    cardLayout.show(panel, name);
    syncDefaultButton();
  }

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

  public void refreshBorrowerList() {
    borrowerListArea.setText(db.getBorrowerCsv());
    rebuildBorrowerRowsUi();
  }

  private void clearAddBorrowerFields() {
    firstNameText.setText("");
    lastNameText.setText("");
    emailText.setText("");
    phoneText.setText("");
  }

  private boolean persistDatabaseQuiet() {
    try {
      db.writeToFile();
      return true;
    } catch (Exception ex) {
      resultLabel.setForeground(LibraryUiTheme.ERROR);
      resultLabel.setText(ex.getMessage() != null ? ex.getMessage() : ex.toString());
      return false;
    }
  }

  private void persistOrBail() {
    persistDatabaseQuiet();
  }

  private void openCheckoutDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(panel);
    JDialog d = new JDialog(w, "Checkout book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = booksTabStyleShell(d, "Checkout book");
    JTextField emailF = LibraryUiTheme.styledTextField();
    JTextField callF = LibraryUiTheme.styledTextField();
    JLabel err = new JLabel(" ");
    err.setForeground(LibraryUiTheme.ERROR);
    JPanel form = twoFieldForm(emailF, callF, "Borrower email", "Book call number");
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(err, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);
    JPanel foot = new JPanel(new BorderLayout());
    foot.setOpaque(false);
    foot.add(cancelLink(d), BorderLayout.WEST);
    RoundedJButton go = new RoundedJButton("Checkout", true);
    go.addActionListener(
        ev -> {
          err.setText(" ");
          String msg =
              CirculationHelper.tryCheckout(db, emailF.getText().trim(), callF.getText().trim());
          if ("ok".equals(msg)) {
            persistOrBail();
            feedback.showMessage("Checkout successful \u2713", LibraryUiTheme.SUCCESS);
            refreshBorrowerList();
            d.dispose();
          } else {
            err.setText(msg);
          }
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(go);
    foot.add(east, BorderLayout.EAST);
    root.add(foot, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private void openReturnDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(panel);
    JDialog d = new JDialog(w, "Return book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = booksTabStyleShell(d, "Return book");
    JTextField emailF = LibraryUiTheme.styledTextField();
    JTextField callF = LibraryUiTheme.styledTextField();
    JLabel err = new JLabel(" ");
    err.setForeground(LibraryUiTheme.ERROR);
    JPanel form = twoFieldForm(emailF, callF, "Borrower email", "Book call number");
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(err, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);
    RoundedJButton go = new RoundedJButton("Return", true);
    JPanel foot = new JPanel(new BorderLayout());
    foot.setOpaque(false);
    foot.add(cancelLink(d), BorderLayout.WEST);
    go.addActionListener(
        ev -> {
          err.setText(" ");
          boolean ok =
              CirculationHelper.tryReturn(db, emailF.getText().trim(), callF.getText().trim());
          if (ok) {
            persistOrBail();
            feedback.showMessage("Return successful \u2713", LibraryUiTheme.SUCCESS);
            refreshBorrowerList();
            d.dispose();
          } else {
            err.setText("Book is not currently checked out.");
          }
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(go);
    foot.add(east, BorderLayout.EAST);
    root.add(foot, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private void openRenewDialog() {
    feedback.clearNow();
    Window w = SwingUtilities.getWindowAncestor(panel);
    JDialog d = new JDialog(w, "Renew book", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
    JPanel root = booksTabStyleShell(d, "Renew book");
    JTextField callF = LibraryUiTheme.styledTextField();
    JLabel err = new JLabel(" ");
    err.setForeground(LibraryUiTheme.ERROR);
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 14, 0);
    JPanel one = labeled("Book call number", callF);
    form.add(one, gbc);
    JPanel mid = new JPanel(new BorderLayout(0, 8));
    mid.setOpaque(false);
    mid.add(form, BorderLayout.NORTH);
    mid.add(err, BorderLayout.CENTER);
    root.add(mid, BorderLayout.CENTER);
    JPanel foot = new JPanel(new BorderLayout());
    foot.setOpaque(false);
    foot.add(cancelLink(d), BorderLayout.WEST);
    RoundedJButton go = new RoundedJButton("Renew", true);
    go.addActionListener(
        ev -> {
          err.setText(" ");
          String call = callF.getText().trim();
          for (String k : CirculationHelper.candidateKeys(call)) {
            Book b = DatabaseView.books(db).get(k);
            if (b == null) {
              continue;
            }
            if (!b.isCheckedOut()) {
              continue;
            }
            if (b.getCheckedOut().isRenewed()) {
              err.setText("Due date can be renewed only once.");
              return;
            }
            if (db.renew(k)) {
              persistOrBail();
              feedback.showMessage("Renew successful", LibraryUiTheme.SUCCESS);
              refreshBorrowerList();
              d.dispose();
              return;
            }
            err.setText("Failed to renew.");
            return;
          }
          err.setText("Failed to renew.");
        });
    JPanel east = new JPanel();
    east.setOpaque(false);
    east.add(go);
    foot.add(east, BorderLayout.EAST);
    root.add(foot, BorderLayout.SOUTH);
    d.pack();
    d.setLocationRelativeTo(w);
    d.setVisible(true);
  }

  private JPanel booksTabStyleShell(JDialog d, String title) {
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

  private JPanel twoFieldForm(JTextField emailF, JTextField callF, String l1, String l2) {
    JPanel form = new JPanel(new GridBagLayout());
    form.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(0, 0, 14, 0);
    gbc.gridy = 0;
    form.add(labeled(l1, emailF), gbc);
    gbc.gridy = 1;
    form.add(labeled(l2, callF), gbc);
    return form;
  }

  private JPanel labeled(String label, JTextField field) {
    JPanel p = new JPanel(new BorderLayout(0, 6));
    p.setOpaque(false);
    JLabel l = new JLabel(label);
    l.setFont(LibraryUiTheme.uiFont(Font.PLAIN, 12));
    l.setForeground(new Color(0x555555));
    p.add(l, BorderLayout.NORTH);
    p.add(field, BorderLayout.CENTER);
    return p;
  }

  private JLabel cancelLink(JDialog d) {
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
}
