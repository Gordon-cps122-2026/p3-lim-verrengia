package library.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Holds all data for the Library, storing it in a file and reading and updating the file as needed.
 *
 * <p>Singleton class: only one instance exists at a time.
 */
public class LibraryDatabase implements java.io.Serializable {
  private static final long serialVersionUID = 1L; // Required for serialization
  private static LibraryDatabase instance; // Singleton instance
  private static final String FILE_NAME = "library.db"; // File to store the database

  private TreeMap<String, Book> books;
  private TreeMap<String, Borrower> borrowers;

  /**
   * Create a LibraryDatabase by reading from a file, or create a new one if the file does not
   * exist.
   */
  public static LibraryDatabase getInstance() {
    return getInstance(false);
  }

  /**
   * In the normal (non-testing) case, create a LibraryDatabase by reading from a file, or create a
   * new one if the file does not exist. In the testing case, return a new empty LibraryDatabase on
   * every call so tests can reset state between runs.
   *
   * @param testing If true, the database always starts empty.
   */
  public static LibraryDatabase getInstance(boolean testing) {

    if (testing) {
      instance = new LibraryDatabase();
      return instance;
    }
    if (instance == null) {
      try {
        readFromFile();
      } catch (FileNotFoundException e) {
        instance = new LibraryDatabase();
      } catch (Throwable e) {
        System.err.println("Unexpected exception " + e);
        e.printStackTrace(System.err);
        System.exit(1);
      }
    }
    return instance;
  }

  /**
   * Save the whole library database to a file. The file name is a constant (since the object is a
   * singleton).
   *
   * @throws Exception
   */
  public void writeToFile() throws Exception {
    writeToFile(null);
  }

  /**
   * Save the whole library database to a file. For testing, an output filename may be provided.
   * However, for normal use, a null filename must be provided and the default filename will be
   * used.
   *
   * @param filename The name of the file to write to. If null, the default filename will be used.
   * @throws Exception
   */
  public void writeToFile(String filename) throws Exception {
    if (filename == null) {
      filename = FILE_NAME;
    }
    ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(filename));
    stream.writeObject(instance);
    stream.close();
  }

  /**
   * Read the whole library database from a file to create the database instance. Must only be
   * called when instance is null (or else it would violate the singleton property).
   *
   * @throws Exception
   */
  private static void readFromFile() throws Exception {
    if (instance != null) {
      throw new Exception("LibraryDatabase already exists");
    }
    ObjectInputStream stream = new ObjectInputStream(new FileInputStream(FILE_NAME));
    instance = (LibraryDatabase) stream.readObject();
    stream.close();
  }

  /** Private constructor creates empty library. */
  private LibraryDatabase() {
    books = new TreeMap<>();
    borrowers = new TreeMap<>();
  }

  // MORE OF YOUR CODE HERE

  // The following methods are provided for testing.
  // You should implement them by using the code you are writing.

  /**
   * Add a book to the library, without knowing anything about objects in the library.
   *
   * @param title The book's title
   * @param author The book's author
   * @param callNumber The book's call number
   * @return true if the book was added, false if a book with the same call number already exists
   */
  public boolean addBook(String title, String author, String callNumber) {
    if ((title == null || title.isEmpty())
        || (author == null || author.isEmpty())
        || (callNumber == null || callNumber.isEmpty())) {
      return false;
    }
    if (books.containsKey(callNumber)) {
      return false;
    }

    Book book = new Book(title, author, callNumber);
    books.put(callNumber, book);
    return true;
  }

  /**
   * Add a borrower to the library, without knowing anything about objects in the library.
   *
   * @param firstName The borrower's first name
   * @param lastName The borrower's last name
   * @param email The borrower's email
   * @param phone The borrower's phone number
   * @return true if the borrower was added, false if a borrower with the same email already exists
   */
  public boolean addBorrower(String firstName, String lastName, String email, String phone) {
    if (borrowers.containsKey(email)) {
      return false;
    }

    borrowers.put(email, new Borrower(firstName, lastName, email, phone));
    return true;
  }

  /**
   * Get callNumbers of all books in the library.
   *
   * @return A sorted set of all callNumbers.
   */
  public NavigableSet<String> getCallNumbers() {
    NavigableSet<String> callNums = new TreeSet<>();
    for (String key : books.keySet()) {
      callNums.add(books.get(key).getCallNumber());
    }
    return callNums;
  }

  /**
   * Get emails of all borrowers in the library.
   *
   * @return A sorted set of all emails.
   */
  public NavigableSet<String> getEmails() {
    NavigableSet<String> emails = new TreeSet<>();

    for (Borrower b : borrowers.values()) {
      emails.add(b.getEmail());
    }

    return emails;
  }

  /**
   * Return a report of all books in the library, in CSV (comma-separated values) format.
   *
   * @return A string containing one line per book in the library, in the form
   *     <p>"title","author","callNumber"
   *     <p>Note that each field is in quotes, and the fields are separated by commas.
   */
  public String getBookCsv() {
    StringBuilder bookCsv = new StringBuilder();
    for (String key : books.keySet()) {
      String title = books.get(key).getTitle();
      String author = books.get(key).getAuthor();
      String callNum = key;
      bookCsv.append("\"" + title + "\",\"" + author + "\",\"" + callNum + "\"\n");
    }

    return bookCsv.toString();
  }

  /**
   * Return a report of all borrowers (library users), in CSV (comma-separated values) format.
   *
   * @return A string containing one line per borrower, in the form
   *     <p>"first name","last name","email","phone"
   *     <p>Note that each field is in quotes, and the fields are separated by commas.
   */
  public String getBorrowerCsv() {
    StringBuilder borrowerCsv = new StringBuilder();
    for (Borrower b : borrowers.values()) {
      borrowerCsv.append(
          "\""
              + b.getFirstName()
              + "\",\""
              + b.getLastName()
              + "\",\""
              + b.getEmail()
              + "\",\""
              + b.getPhone()
              + "\"\n");
    }

    return borrowerCsv.toString();
  }

  public boolean checkout(String email, String callNumber) {
    Book book = books.get(callNumber);
    Borrower borrower = borrowers.get(email);

    if (book.isCheckedOut()) {
      return false;
    } else {
      CheckedOut checkedOut = new CheckedOut(book, borrower);

      book.addCheckedOut(email, checkedOut);
      borrower.addCheckedOut(callNumber, checkedOut);

      return true;
    }
  }

  public boolean renewDueDate(String email, String callNumber) {
    Book book = books.get(callNumber);
    if (!(book.isCheckedOut())) {
      return false;
    }

    CheckedOut checkedOut = book.getCheckedOut(email);
    if (checkedOut.isRenewed()) {
      return false;
    } else {
      checkedOut.renewDueDate();
      return true;
    }
  }

  public boolean returnBook(String email, String callNumber) {
    Book book = books.get(callNumber);
    Borrower borrower = borrowers.get(email);

    if (book.isCheckedOut()) {
      book.removeCheckedOut(email);
      borrower.removeCheckedOut(callNumber);
      return true;
    }

    return false;
  }
}
