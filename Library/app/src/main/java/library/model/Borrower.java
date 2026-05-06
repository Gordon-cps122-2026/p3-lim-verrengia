package library.model;

import java.util.*;

public class Borrower implements java.io.Serializable {
  // Required for serialization
  private static final long serialVersionUID = 1L;

  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private TreeMap<String, CheckedOut> checkedOut;

  /**
   * Creates a Borrower with the given name, email, and phone number.
   *
   * @param firstName The borrower's first name
   * @param lastName The borrower's last name
   * @param email The borrower's email
   * @param phone The borrower's phone number
   */
  public Borrower(String firstName, String lastName, String email, String phone) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.checkedOut = new TreeMap<>();
  }

  /**
   * Gets the borrower's first name.
   *
   * @return the borrower's first name
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Gets the borrower's last name.
   *
   * @return the borrower's last name
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * Gets the borrower's email.
   *
   * @return the borrower's email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Gets the borrower's phone number.
   *
   * @return the borrower's phone number
   */
  public String getPhone() {
    return phone;
  }

  /**
   * Adds a CheckedOut record in this borrower using the book's call number as the key.
   *
   * @param callNumber The call number of the checked out book
   * @param newCheckedOut The CheckedOut record to associate
   * @return true if the record was successfully added
   */
  public boolean addCheckedOut(String key, CheckedOut newCheckedOut) {
    checkedOut.put(key, newCheckedOut);
    return true;
  }

  /**
   * Gets the CheckedOut record for a specific book.
   *
   * @param callNumber The call number of the book
   * @return the CheckedOut record, or null if the book is not checked out by this borrower
   */
  public CheckedOut getCheckedOut(String callNumber) {
    return checkedOut.get(callNumber);
  }

  /**
   * Removes the CheckedOut record for a specific book from this borrower.
   *
   * @param callNumber The call number of the book to remove
   * @return true if the record was successfully removed
   */
  public boolean removeCheckedOut(String callNumber) {
    checkedOut.remove(callNumber);
    return true;
  }

  /**
   * Checks whether this borrower has any books currently checked out.
   *
   * @return true if the borrower has at least one book checked out, false otherwise
   */
  public boolean hasCheckedOut() {
    if (checkedOut.isEmpty()) {
      return false;
    }
    return true;
  }

  public boolean isNull() {
    return (firstName.isEmpty()
        || firstName == null
        || lastName.isEmpty()
        || lastName == null
        || email.isEmpty()
        || email == null
        || phone.isEmpty()
        || phone == null);
  }

  /**
   * Gets a collection of all books currently checked out by this borrower.
   *
   * @return a collection of checked out books, or an empty collection if none are checked out
   */
  public Collection<Book> getAllBooks() {
    Collection<Book> result = new ArrayList<>();

    if (hasCheckedOut()) {
      for (CheckedOut i : checkedOut.values()) {
        result.add(i.getBook());
      }
    }
    return result;
  }
}
