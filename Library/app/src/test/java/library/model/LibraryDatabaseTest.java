package library.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LibraryDatabaseTest {
  private TreeSet<String> callNumbers;
  private TreeSet<String> emails;
  private LibraryDatabase db;

  @BeforeEach
  public void setUp() {
    db = LibraryDatabase.getInstance(true);
    db.addBook("Title1", "Author1", "CallNumber1");
    db.addBook("Title2", "Author2", "CallNumber2");
    db.addBook("Title3", "Author3", "CallNumber3");
    callNumbers = new TreeSet<String>();
    callNumbers.add("CallNumber1");
    callNumbers.add("CallNumber2");
    callNumbers.add("CallNumber3");
    db.addBorrower("FirstName1", "LastName1", "Email1", "Phone1");
    db.addBorrower("FirstName2", "LastName2", "Email2", "Phone2");
    emails = new TreeSet<String>();
    emails.add("Email1");
    emails.add("Email2");
  }

  @Test
  public void testAddBook() {
    assertTrue(callNumbers.tailSet("").equals(db.getCallNumbers()));
  }

  @Test
  public void testAddBorrower() {
    assertTrue(emails.tailSet("").equals(db.getEmails()));
  }

  @Test
  public void testGetBookCsv() {
    String expectedCsv =
        "\"Title1\",\"Author1\",\"CallNumber1\"\n"
            + "\"Title2\",\"Author2\",\"CallNumber2\"\n"
            + "\"Title3\",\"Author3\",\"CallNumber3\"\n";
    assertEquals(expectedCsv, db.getBookCsv());
  }

  @Test
  public void testGetBorrowerCsv() {
    String expectedCsv =
        "\"FirstName1\",\"LastName1\",\"Email1\",\"Phone1\"\n"
            + "\"FirstName2\",\"LastName2\",\"Email2\",\"Phone2\"\n";
    assertEquals(expectedCsv, db.getBorrowerCsv());
  }

  // It's possible to write good tests for writeToFile() and readFromFile(), but it would
  // complicate your LibraryDatabase code. The problem is that de-serialization (in readFromFile())
  // creates a new instance of LibraryDatabase, which is a singleton.  But changing that would
  // require making the class more complicated to write.  So for now, simply trust that I have
  // provided working code for them, and be sure to test all the code you write.

  // Nevertheless, we can do a little testing of writeToFile().
  @Test
  public void testWriteToFile() {
    try {
      db.writeToFile("testLibraryOutput.db");
    } catch (Exception e) {
      fail("Exception thrown while writing to file: " + e.getMessage());
    }
    // Check if the file was created
    File file = new File("testLibraryOutput.db");
    assertTrue(file.exists(), "Output file should exist after writing.");
    // Leave the test file, in case it's useful for debugging
    // Checking the contents is complicated, because de-serializing creates a new object, but the
    // class is a singleton.  For now, we won't complicate the class to work around that.
  }
}
