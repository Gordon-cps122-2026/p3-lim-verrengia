package library.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.CardLayout;
import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.*;
import library.model.LibraryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Note to students: it might be necessary to rename some things as
// noted in comments below.  Try not make any other changes to the
// existing tests, but you may add more tests.  (If you find other
// things that renaming, add a comment about the reason for that change.)

public class BorrowersTabTest {
  // Note to students: if your Books tab is in a class with another name
  // instead of Books.java, change Books here and in subsequent uses
  // to the proper class name.  (AI is helpful for renaming.)

  private BorrowersTab borrowers;
  private LibraryDatabase db;

  @BeforeEach
  public void setUp() {
    // Use test mode to avoid reading/writing files
    db = LibraryDatabase.getInstance(true);
    borrowers = new BorrowersTab(db);
  }

  @Test
  public void testInitialization() {
    // Verify panel is created
    JPanel panel = borrowers.getPanel();
    assertNotNull(panel, "Main panel should be created");

    // Verify panel uses CardLayout
    assertTrue(panel.getLayout() instanceof CardLayout, "Panel should use CardLayout");
  }

  @Test
  public void testPanelComponents() {
    JPanel panel = borrowers.getPanel();

    // Check that both cards exist in the panel
    boolean hasNewBorrowerCard = false;
    boolean hasBorrowersCard = false;

    for (Component comp : panel.getComponents()) {
      if (comp instanceof JPanel) {
        JPanel card = (JPanel) comp;
        // Look for key components that identify each card
        for (Component c : card.getComponents()) {
          if (c instanceof JLabel && "New Borrower".equals(((JLabel) c).getText())) {
            hasNewBorrowerCard = true;
          }
          if (c instanceof JLabel && "Borrowers".equals(((JLabel) c).getText())) {
            hasBorrowersCard = true;
          }
        }
      }
    }

    assertTrue(hasNewBorrowerCard, "Should have a 'New Borrower' card");
    assertTrue(hasBorrowersCard, "Should have a 'Borrowers' card");
  }

  @Test
  public void testAddBorrower() {
    // Add a test borrower to the database
    boolean added = db.addBorrower("John", "Doe", "john@example.com", "555-1234");
    assertTrue(added, "Should add borrower successfully");

    // Verify the borrower exists in the database
    String borrowerList = db.getBorrowerCsv();
    assertTrue(borrowerList.contains("\"John\""), "Borrower list should contain the first name");
    assertTrue(borrowerList.contains("\"Doe\""), "Borrower list should contain the last name");
    assertTrue(
        borrowerList.contains("\"john@example.com\""), "Borrower list should contain the email");
  }

  @Test
  public void testDuplicateBorrower() {
    // Add first borrower
    boolean firstAdded = db.addBorrower("John", "Doe", "john@example.com", "555-1234");
    assertTrue(firstAdded, "First borrower should be added successfully");

    // Try to add duplicate (same email)
    boolean duplicateAdded = db.addBorrower("Johnny", "Doe", "john@example.com", "555-5678");
    assertFalse(duplicateAdded, "Duplicate borrower should not be added");
  }

  @Test
  public void testTextFieldsExist() throws Exception {
    // Access text field components using reflection
    Field firstNameTextField = BorrowersTab.class.getDeclaredField("firstNameText");
    firstNameTextField.setAccessible(true);
    JTextField firstNameText = (JTextField) firstNameTextField.get(borrowers);

    Field lastNameTextField = BorrowersTab.class.getDeclaredField("lastNameText");
    lastNameTextField.setAccessible(true);
    JTextField lastNameText = (JTextField) lastNameTextField.get(borrowers);

    Field emailTextField = BorrowersTab.class.getDeclaredField("emailText");
    emailTextField.setAccessible(true);
    JTextField emailText = (JTextField) emailTextField.get(borrowers);

    Field phoneTextField = BorrowersTab.class.getDeclaredField("phoneText");
    phoneTextField.setAccessible(true);
    JTextField phoneText = (JTextField) phoneTextField.get(borrowers);

    // Check that text fields exist
    assertNotNull(firstNameText);
    assertNotNull(lastNameText);
    assertNotNull(emailText);
    assertNotNull(phoneText);
  }

  @Test
  public void testBorrowerListDisplayAddBorrower() throws Exception {
    // Get the borrower list text area using reflection
    Field borrowerListAreaField = BorrowersTab.class.getDeclaredField("borrowerListArea");
    borrowerListAreaField.setAccessible(true);
    JTextArea borrowerListArea = (JTextArea) borrowerListAreaField.get(borrowers);

    // Check that the text area is initially empty
    assertNotNull(borrowerListArea);
    String expectedText = "";
    assertEquals(expectedText, borrowerListArea.getText());

    // Click the "Add Borrower" button in borrowers card
    Field borrowersCardField = BorrowersTab.class.getDeclaredField("borrowersCard");
    borrowersCardField.setAccessible(true);
    JPanel borrowersCard = (JPanel) borrowersCardField.get(borrowers);
    JButton addBorrowerButton = findButtonByText(borrowersCard, "Add Borrower");
    assertNotNull(addBorrowerButton);
    addBorrowerButton.doClick();

    // Simulate entering borrower details
    Field firstNameTextField = BorrowersTab.class.getDeclaredField("firstNameText");
    firstNameTextField.setAccessible(true);
    JTextField firstNameText = (JTextField) firstNameTextField.get(borrowers);
    firstNameText.setText("John");

    Field lastNameTextField = BorrowersTab.class.getDeclaredField("lastNameText");
    lastNameTextField.setAccessible(true);
    JTextField lastNameText = (JTextField) lastNameTextField.get(borrowers);
    lastNameText.setText("Doe");

    Field emailTextField = BorrowersTab.class.getDeclaredField("emailText");
    emailTextField.setAccessible(true);
    JTextField emailText = (JTextField) emailTextField.get(borrowers);
    emailText.setText("john.doe@example.com");

    Field phoneTextField = BorrowersTab.class.getDeclaredField("phoneText");
    phoneTextField.setAccessible(true);
    JTextField phoneText = (JTextField) phoneTextField.get(borrowers);
    phoneText.setText("555-1234");

    // Simulate clicking the "Add Borrower" button in the new borrower card
    Field newBorrowerCardField = BorrowersTab.class.getDeclaredField("newBorrowerCard");
    newBorrowerCardField.setAccessible(true);
    JPanel newBorrowerCard = (JPanel) newBorrowerCardField.get(borrowers);
    JButton addButton = findButtonByText(newBorrowerCard, "Add Borrower");
    assertNotNull(addButton);
    addButton.doClick();

    // Simulate clicking the Back to Borrowers button
    JButton backButton = findButtonByText(newBorrowerCard, "Back to Borrowers");
    assertNotNull(backButton);
    backButton.doClick();

    // Check that the text area shows the borrower
    assertNotNull(borrowerListArea);
    String displayedText = borrowerListArea.getText();
    assertTrue(displayedText.contains("\"John\""), "Display should contain first name");
    assertTrue(displayedText.contains("\"Doe\""), "Display should contain last name");
    assertTrue(displayedText.contains("\"john.doe@example.com\""), "Display should contain email");
    assertTrue(displayedText.contains("\"555-1234\""), "Display should contain phone");
  }

  @Test
  public void testCardSwitching() throws Exception {
    // Get necessary fields using reflection
    Field cardLayoutField = BorrowersTab.class.getDeclaredField("cardLayout");
    cardLayoutField.setAccessible(true);
    Field currentCardField = BorrowersTab.class.getDeclaredField("currentCard");
    currentCardField.setAccessible(true);

    // Initially we should be on the borrowers card
    assertEquals("borrowers", currentCardField.get(borrowers));

    // Find the "Add Borrower" button on borrowers card
    Field borrowersCardField = BorrowersTab.class.getDeclaredField("borrowersCard");
    borrowersCardField.setAccessible(true);
    JPanel borrowersCard = (JPanel) borrowersCardField.get(borrowers);

    JButton addButton = findButtonByText(borrowersCard, "Add Borrower");
    assertNotNull(addButton);

    // Simulate button click
    addButton.doClick();

    // Should switch to newBorrower card
    assertEquals("newBorrower", currentCardField.get(borrowers));

    // Find the "Back to Borrowers" button on new borrower card
    Field newBorrowerCardField = BorrowersTab.class.getDeclaredField("newBorrowerCard");
    newBorrowerCardField.setAccessible(true);
    JPanel newBorrowerCard = (JPanel) newBorrowerCardField.get(borrowers);
    JButton backButton = findButtonByText(newBorrowerCard, "Back to Borrowers");
    assertNotNull(backButton);

    // Simulate button click
    backButton.doClick();

    // Should switch back to borrowers card
    assertEquals("borrowers", currentCardField.get(borrowers));
  }

  @Test
  public void testResultLabelUpdatesOnAdd() throws Exception {
    // Access the result label using reflection
    Field resultLabelField = BorrowersTab.class.getDeclaredField("resultLabel");
    resultLabelField.setAccessible(true);
    JLabel resultLabel = (JLabel) resultLabelField.get(borrowers);

    // Switch to the newBorrower card
    Field currentCardField = BorrowersTab.class.getDeclaredField("currentCard");
    currentCardField.setAccessible(true);
    currentCardField.set(borrowers, "newBorrower");

    // Set text in the input fields
    Field firstNameTextField = BorrowersTab.class.getDeclaredField("firstNameText");
    firstNameTextField.setAccessible(true);
    JTextField firstNameText = (JTextField) firstNameTextField.get(borrowers);
    firstNameText.setText("Jane");

    Field lastNameTextField = BorrowersTab.class.getDeclaredField("lastNameText");
    lastNameTextField.setAccessible(true);
    JTextField lastNameText = (JTextField) lastNameTextField.get(borrowers);
    lastNameText.setText("Smith");

    Field emailTextField = BorrowersTab.class.getDeclaredField("emailText");
    emailTextField.setAccessible(true);
    JTextField emailText = (JTextField) emailTextField.get(borrowers);
    emailText.setText("jane.smith@example.com");

    Field phoneTextField = BorrowersTab.class.getDeclaredField("phoneText");
    phoneTextField.setAccessible(true);
    JTextField phoneText = (JTextField) phoneTextField.get(borrowers);
    phoneText.setText("555-5678");

    // Simulate clicking the Add Borrower button
    Field addBorrowerButtonField = BorrowersTab.class.getDeclaredField("addBorrowerButton");
    addBorrowerButtonField.setAccessible(true);
    JButton addBorrowerButton = (JButton) addBorrowerButtonField.get(borrowers);
    addBorrowerButton.doClick();

    // Verify the result label was updated
    String resultText = resultLabel.getText();
    assertTrue(resultText.contains("Added borrower:"), "Result label should show success message");
    assertTrue(resultText.contains("Jane Smith"), "Result label should contain borrower name");
    assertTrue(resultText.contains("jane.smith@example.com"), "Result label should contain email");
  }

  @Test
  public void testDefaultButtonBehavior() throws Exception {
    // Create a root pane for testing
    JRootPane testRootPane = new JRootPane();

    // Access fields using reflection
    Field rootPaneField = BorrowersTab.class.getDeclaredField("rootPane");
    rootPaneField.setAccessible(true);

    Field addBorrowerButtonField = BorrowersTab.class.getDeclaredField("addBorrowerButton");
    addBorrowerButtonField.setAccessible(true);
    JButton addBorrowerButton = (JButton) addBorrowerButtonField.get(borrowers);

    // Set root pane while on borrowers card
    borrowers.setRootPane(testRootPane);
    assertNull(testRootPane.getDefaultButton(), "No default button on borrowers card");

    // Switch to newBorrower card
    Field currentCardField = BorrowersTab.class.getDeclaredField("currentCard");
    currentCardField.setAccessible(true);
    currentCardField.set(borrowers, "newBorrower");

    // Update the root pane
    borrowers.setRootPane(testRootPane);
    assertEquals(
        addBorrowerButton,
        testRootPane.getDefaultButton(),
        "Add Borrower button should be default on newBorrower card");
  }

  // Helper method to find button by text
  private JButton findButtonByText(JPanel panel, String text) {
    for (Component comp : panel.getComponents()) {
      if (comp instanceof JButton && text.equals(((JButton) comp).getText())) {
        return (JButton) comp;
      } else if (comp instanceof JPanel) {
        JButton button = findButtonByText((JPanel) comp, text);
        if (button != null) {
          return button;
        }
      }
    }
    return null;
  }
}
