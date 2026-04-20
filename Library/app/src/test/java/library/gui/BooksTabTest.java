package library.gui;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Component;
import java.lang.reflect.Field;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import library.model.LibraryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Note to students: it might be necessary to rename some things as
// noted in comments below.  Try not make any other changes to the
// existing tests, but you may add more tests.  (If you find other
// things that renaming, add a comment about the reason for that change.)

public class BooksTabTest {
  // Note to students: if your Books tab is in a class with another name
  // instead of Books.java, change Books here and in subsequent uses
  // to the proper class name.  (AI is helpful for renaming.)
  private BooksTab books;
  private LibraryDatabase db;

  @BeforeEach
  public void setUp() throws Exception {
    // Use test mode to avoid reading/writing files
    db = LibraryDatabase.getInstance(true);
    books = new BooksTab(db);
  }

  @Test
  public void testTextFieldsExist() throws Exception {
    // Access text field components
    Field titleTextField = BooksTab.class.getDeclaredField("titleText");
    titleTextField.setAccessible(true);
    JTextField titleText = (JTextField) titleTextField.get(books);

    Field authorTextField = BooksTab.class.getDeclaredField("authorText");
    authorTextField.setAccessible(true);
    JTextField authorText = (JTextField) authorTextField.get(books);

    Field callNumberTextField = BooksTab.class.getDeclaredField("callNumberText");
    callNumberTextField.setAccessible(true);
    JTextField callNumberText = (JTextField) callNumberTextField.get(books);

    // Check that text fields exist
    assertNotNull(titleText);
    assertNotNull(authorText);
    assertNotNull(callNumberText);
  }

  @Test
  public void testBookListDisplayAddBook() throws Exception {
    // Get the book list text area
    Field bookListAreaField = BooksTab.class.getDeclaredField("bookListArea");
    bookListAreaField.setAccessible(true);
    JTextArea bookListArea = (JTextArea) bookListAreaField.get(books);

    // Check that the text area is empty
    assertNotNull(bookListArea);
    String expectedText = "";
    assertEquals(expectedText, bookListArea.getText());

    // Click the "Add Book" button in collection card
    Field collectionCardField = BooksTab.class.getDeclaredField("collectionCard");
    collectionCardField.setAccessible(true);
    JPanel collectionCard = (JPanel) collectionCardField.get(books);
    JButton addBookButton = findButtonByText(collectionCard, "Add Book");
    assertNotNull(addBookButton);
    addBookButton.doClick();

    // Simulate entering book details
    Field callNumberTextField = BooksTab.class.getDeclaredField("callNumberText");
    callNumberTextField.setAccessible(true);
    JTextField callNumberText = (JTextField) callNumberTextField.get(books);
    callNumberText.setText("TEST001");
    Field titleTextField = BooksTab.class.getDeclaredField("titleText");
    titleTextField.setAccessible(true);
    JTextField titleText = (JTextField) titleTextField.get(books);
    titleText.setText("Test Title");
    Field authorTextField = BooksTab.class.getDeclaredField("authorText");
    authorTextField.setAccessible(true);
    JTextField authorText = (JTextField) authorTextField.get(books);
    authorText.setText("Test Author");

    // Simulate clicking the "Add Book" button in hte new book card
    Field newBookCardField = BooksTab.class.getDeclaredField("newBookCard");
    newBookCardField.setAccessible(true);
    JPanel newBookCard = (JPanel) newBookCardField.get(books);
    JButton addBookButtonNew = findButtonByText(newBookCard, "Add Book");
    assertNotNull(addBookButtonNew);
    addBookButtonNew.doClick();

    // Simulate clicking the Back to Collection button
    JButton backButton = findButtonByText(newBookCard, "Back to Collection");
    assertNotNull(backButton);
    backButton.doClick();

    // Check that the text area shows the book
    assertNotNull(bookListArea);
    expectedText = "\"Test Title\",\"Test Author\",\"TEST001\"\n";
    assertEquals(expectedText, bookListArea.getText());
  }

  @Test
  public void testCardSwitching() throws Exception {
    // Get necessary fields
    Field cardLayoutField = BooksTab.class.getDeclaredField("cardLayout");
    cardLayoutField.setAccessible(true);
    Field currentCardField = BooksTab.class.getDeclaredField("currentCard");
    currentCardField.setAccessible(true);

    // Initially we should be on the collection card
    assertEquals("collection", currentCardField.get(books));

    // Find the "Add Book" button on collection card
    Field collectionCardField = BooksTab.class.getDeclaredField("collectionCard");
    collectionCardField.setAccessible(true);
    JPanel collectionCard = (JPanel) collectionCardField.get(books);

    JButton addButton = findButtonByText(collectionCard, "Add Book");
    assertNotNull(addButton);

    // Simulate button click
    addButton.doClick();

    // Should switch to newBook card
    assertEquals("newBook", currentCardField.get(books));

    // Find the "Back to Collection" button on new book card
    Field newBookCardField = BooksTab.class.getDeclaredField("newBookCard");
    newBookCardField.setAccessible(true);
    JPanel newBookCard = (JPanel) newBookCardField.get(books);
    JButton backButton = findButtonByText(newBookCard, "Back to Collection");
    assertNotNull(backButton);
    // Simulate button click
    backButton.doClick();
    // Should switch back to collection card
    assertEquals("collection", currentCardField.get(books));
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
