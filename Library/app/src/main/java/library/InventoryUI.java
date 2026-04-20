package library;

import library.model.LibraryDatabase;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * GUI for the Library management system using Swing.
 * Provides interface for viewing and managing books and borrowers.
 */
public class InventoryUI {
    private JFrame frame;
    private JTabbedPane tabbedPane;
    private LibraryDatabase database;
    
    // Books tab components
    private JTextArea bookListArea;
    
    // Borrowers tab components
    private JTextArea borrowersListArea;
    
    // Add Book form components
    private JTextField titleText;
    private JTextField authorText;
    private JTextField callNumberText;
    
    // Add Borrower form components
    private JTextField firstNameText;
    private JTextField lastNameText;
    private JTextField emailText;
    private JTextField phoneText;
    
    // Panels for different views
    private JPanel booksTabPanel;
    private JPanel borrowersTabPanel;
    private JPanel addBookPanel;
    private JPanel addBorrowerPanel;
    
    /**
     * Constructor for InventoryUI.
     * 
     * @param database The LibraryDatabase instance to manage
     */
    public InventoryUI(LibraryDatabase database) {
        this.database = database;
        initializeUI();
    }
    
    /**
     * Initialize the entire UI.
     */
    private void initializeUI() {
        frame = new JFrame("Library App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create tabs
        createBooksTab();
        createBorrowersTab();
        
        tabbedPane.addTab("Books", booksTabPanel);
        tabbedPane.addTab("Borrowers", borrowersTabPanel);
        
        frame.add(tabbedPane);
        frame.setVisible(true);
        
        refreshBooksDisplay();
        refreshBorrowersDisplay();
    }
    
    /**
     * Create the Books tab with book list and add button.
     */
    private void createBooksTab() {
        booksTabPanel = new JPanel(new BorderLayout());
        
        // Create text area for book list
        bookListArea = new JTextArea();
        bookListArea.setEditable(false);
        bookListArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(bookListArea);
        booksTabPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel at bottom
        JPanel buttonPanel = new JPanel();
        JButton addBookButton = new JButton("Add Book");
        addBookButton.addActionListener(e -> showAddBookForm());
        buttonPanel.add(addBookButton);
        
        booksTabPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Create the Borrowers tab with borrower list and add button.
     */
    private void createBorrowersTab() {
        borrowersTabPanel = new JPanel(new BorderLayout());
        
        // Create title label
        JLabel titleLabel = new JLabel("Borrower Collection");
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        borrowersTabPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Create text area for borrower list
        borrowersListArea = new JTextArea();
        borrowersListArea.setEditable(false);
        borrowersListArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(borrowersListArea);
        borrowersTabPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Create button panel at bottom
        JPanel buttonPanel = new JPanel();
        JButton addBorrowerButton = new JButton("Add Borrower");
        addBorrowerButton.addActionListener(e -> showAddBorrowerForm());
        buttonPanel.add(addBorrowerButton);
        
        borrowersTabPanel.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Display the Add Book form.
     */
    private void showAddBookForm() {
        // Create a new frame for adding a book
        JFrame addBookFrame = new JFrame("New Book");
        addBookFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addBookFrame.setSize(400, 300);
        addBookFrame.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("New Book");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel with fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title field
        formPanel.add(new JLabel("Title:"));
        titleText = new JTextField();
        formPanel.add(titleText);
        
        // Author field
        formPanel.add(new JLabel("Author:"));
        authorText = new JTextField();
        formPanel.add(authorText);
        
        // Call Number field
        formPanel.add(new JLabel("Call Number:"));
        callNumberText = new JTextField();
        formPanel.add(callNumberText);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back to Collection");
        backButton.addActionListener(e -> {
            addBookFrame.dispose();
            tabbedPane.setSelectedIndex(0);
        });
        
        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> {
            String title = titleText.getText();
            String author = authorText.getText();
            String callNumber = callNumberText.getText();
            
            if (title.isEmpty() || author.isEmpty() || callNumber.isEmpty()) {
                JOptionPane.showMessageDialog(addBookFrame, 
                    "Please fill in all fields.", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            } else if (database.addBook(title, author, callNumber)) {
                refreshBooksDisplay();
                addBookFrame.dispose();
                tabbedPane.setSelectedIndex(0);
            } else {
                JOptionPane.showMessageDialog(addBookFrame, 
                    "Failed to add book. A book with this call number may already exist.", 
                    "Add Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBookFrame.add(mainPanel);
        addBookFrame.setVisible(true);
    }
    
    /**
     * Display the Add Borrower form.
     */
    private void showAddBorrowerForm() {
        // Create a new frame for adding a borrower
        JFrame addBorrowerFrame = new JFrame("New Borrower");
        addBorrowerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addBorrowerFrame.setSize(400, 350);
        addBorrowerFrame.setLocationRelativeTo(frame);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Title label
        JLabel titleLabel = new JLabel("New Borrower");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel with fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // First Name field
        formPanel.add(new JLabel("First Name:"));
        firstNameText = new JTextField();
        formPanel.add(firstNameText);
        
        // Last Name field
        formPanel.add(new JLabel("Last Name:"));
        lastNameText = new JTextField();
        formPanel.add(lastNameText);
        
        // Email field
        formPanel.add(new JLabel("Email:"));
        emailText = new JTextField();
        formPanel.add(emailText);
        
        // Phone field
        formPanel.add(new JLabel("Phone:"));
        phoneText = new JTextField();
        formPanel.add(phoneText);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        JButton backButton = new JButton("Back to Collection");
        backButton.addActionListener(e -> {
            addBorrowerFrame.dispose();
            tabbedPane.setSelectedIndex(1);
        });
        
        JButton addButton = new JButton("Add Borrower");
        addButton.addActionListener(e -> {
            String firstName = firstNameText.getText();
            String lastName = lastNameText.getText();
            String email = emailText.getText();
            String phone = phoneText.getText();
            
            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(addBorrowerFrame, 
                    "Please fill in all fields.", 
                    "Input Error", 
                    JOptionPane.WARNING_MESSAGE);
            } else if (database.addBorrower(firstName, lastName, email, phone)) {
                refreshBorrowersDisplay();
                addBorrowerFrame.dispose();
                tabbedPane.setSelectedIndex(1);
            } else {
                JOptionPane.showMessageDialog(addBorrowerFrame, 
                    "Failed to add borrower. A borrower with this email may already exist.", 
                    "Add Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(backButton);
        buttonPanel.add(addButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        addBorrowerFrame.add(mainPanel);
        addBorrowerFrame.setVisible(true);
    }
    
    /**
     * Refresh the books display to show current books.
     */
    private void refreshBooksDisplay() {
        String booksCsv = database.getBookCsv();
        if (booksCsv.isEmpty()) {
            bookListArea.setText("No books in collection.\n");
        } else {
            // Parse CSV and format for display
            StringBuilder display = new StringBuilder();
            String[] lines = booksCsv.split("\n");
            for (String line : lines) {
                if (!line.isEmpty()) {
                    // Remove quotes and format nicely
                    String[] parts = line.split("\",\"");
                    if (parts.length == 3) {
                        String title = parts[0].replaceAll("\"", "");
                        String author = parts[1].replaceAll("\"", "");
                        String callNum = parts[2].replaceAll("\"", "");
                        display.append(String.format("%-30s | %-25s | %s%n", title, author, callNum));
                    }
                }
            }
            bookListArea.setText(display.toString());
        }
        bookListArea.setCaretPosition(0);
    }
    
    /**
     * Refresh the borrowers display to show current borrowers.
     */
    private void refreshBorrowersDisplay() {
        String borrowersCsv = database.getBorrowerCsv();
        if (borrowersCsv.isEmpty()) {
            borrowersListArea.setText("No borrowers in collection.\n");
        } else {
            // Parse CSV and format for display
            StringBuilder display = new StringBuilder();
            String[] lines = borrowersCsv.split("\n");
            for (String line : lines) {
                if (!line.isEmpty()) {
                    // Remove quotes and format nicely
                    String[] parts = line.split("\",\"");
                    if (parts.length == 4) {
                        String firstName = parts[0].replaceAll("\"", "");
                        String lastName = parts[1].replaceAll("\"", "");
                        String email = parts[2].replaceAll("\"", "");
                        String phone = parts[3].replaceAll("\"", "");
                        display.append(String.format("%-15s | %-15s | %-25s | %s%n", 
                            firstName, lastName, email, phone));
                    }
                }
            }
            borrowersListArea.setText(display.toString());
        }
        borrowersListArea.setCaretPosition(0);
    }
    
    /**
     * Get the main frame.
     * 
     * @return The JFrame for this UI
     */
    public JFrame getFrame() {
        return frame;
    }
}
