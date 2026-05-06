package library.model;

import java.util.TreeMap;

public class Book implements java.io.Serializable {

  private String title;
  private String author;
  private String callNumber;
  private static final long serialVersionUID = 1L;
  private TreeMap<String, CheckedOut> checkedOut;
  private int copy;
  private String key;

  /**
   * Creates a Book with the given title, author, call number, and copy number.
   *
   * @param title The book's title
   * @param author The book's author
   * @param callNumber The book's call number
   * @param copy The copy number of this book
   */
  public Book(String title, String author, String callNumber, int copy) {
    this.title = title;
    this.author = author;
    this.callNumber = callNumber;
    checkedOut = new TreeMap<>();
    this.copy = copy;
    this.key = callNumber + Integer.toString(copy);
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
   * Gets the copy number of this book.
   *
   * @return the copy number of this book
   */
  public int getCopy(){
    return copy;
  }

  /**
   * Gets the unique key for this book, which is the call number concatenated with the copy number.
   *
   * @return the key of this book
   */
  public String getKey(){
    return key;
  }

  /**
   * Checks whether the book is currently checked out.
   *
   * @return true if the book is checked out, false otherwise
   */
  public boolean isCheckedOut(){
    if (checkedOut.isEmpty()) {
      return false;
    }
      return true;
  }

  /**
   * Checks whether this book has any null fields.
   *
   * @return true if any field is null or empty, false otherwise
   */
  public boolean isNull(){
    return title.isEmpty()||title==null||author.isEmpty()||author==null||callNumber.isEmpty()||callNumber==null||copy==0;
  }

  /**
   * Adds a CheckedOut record to this book using the borrower's email as the key.
   *
   * @param borrowerEmail The email of the borrower checking out this book
   * @param newCheckedOut The CheckedOut record to associate with this book
   * @return true if the record was successfully added
   */
  public boolean addCheckedOut(String borrowerEmail, CheckedOut newCheckedOut){
    checkedOut.put(borrowerEmail, newCheckedOut);
    return true;
  }

  /**
   * Gets the CheckedOut record associated with this book.
   *
   * @return the CheckedOut record for this book, or null if the book is not checked out
   */
  public CheckedOut getCheckedOut(){
    for(CheckedOut result : checkedOut.values()){
      if(result.getBook()==this){
        return result;
      }
    }
    return null;
  }

  /**
   * Removes the CheckedOut record for the given borrower from this book.
   *
   * @param borrowerEmail The email of the borrower returning this book
   * @return true if the record was successfully removed
   */
  public boolean removeCheckedOut(String borrowerEmail){
    checkedOut.remove(borrowerEmail);
    return true;
  }
}
