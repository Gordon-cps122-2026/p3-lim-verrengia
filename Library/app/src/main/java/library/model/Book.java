package library.model;

public class Book implements java.io.Serializable {

  //TODO make a system that can check if the due date is expired

  private String title;
  private String author;
  private String callNumber;
  private static final long serialVersionUID = 1L;
  private CheckedOut checkedOut;

  /**
  * Creates a Book with the given title, author, and call number.
  *
  * @param title The book's title
  * @param author The book's author
  * @param callNumber The book's call number
  */
  public Book(String title, String author, String callNumber) {
    this.title = title;
    this.author = author;
    this.callNumber = callNumber;
  }

  /**
  * Gets the title of the book.
  *
  * @return the title of the book
  */
  public String getTitle() {
    return title;
  }

  /**
  * Gets the author of the book.
  *
  * @return the author of the book
  */
  public String getAuthor() {
    return author;
  }

  /**
  * Gets the call number of the book.
  *
  * @return the call number of the book
  */
  public String getCallNumber() {
    return callNumber;
  }

  /**
  * Checks whether the book is currently checked out.
  *
  * @return true if the book is checked out, false otherwise
  */
  public boolean isCheckedOut(){
    if (checkedOut == null) {
      return false;
    }
      return true;
  }

  /**
  * Adds a CheckedOut record in this book object.
  *
  * @param callNumber The call number of this book
  * @param newCheckedOut The CheckedOut record to associate
  * @return true if the record was successfully added
  */
  public boolean addCheckedOut(String callNumber, CheckedOut newCheckedOut){
    checkedOut = newCheckedOut;
    return true;
  }

  /**
  * Gets the CheckedOut record associated with this book.
  *
  * @return the CheckedOut record, or null if the book is not checked out
  */
  public CheckedOut getCheckedOut(){
    return checkedOut;
  }

  /**
  * Removes the CheckedOut record from this book, marking it available again.
  *
  * @param callNumber The call number of this book
  * @return true if the record was successfully removed
  */
  public boolean removeCheckedOut(String callNumber){
    checkedOut = null;
    return true;
  }
}
