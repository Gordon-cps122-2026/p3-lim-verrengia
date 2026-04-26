package library.model;

import java.util.TreeMap;

public class Book implements java.io.Serializable {

  private String title;
  private String author;
  private String callNumber;
  private static final long serialVersionUID = 1L;
  private TreeMap<String, CheckedOut> checkedOut;

  public Book(String title, String author, String callNumber) {
    this.title = title;
    this.author = author;
    this.callNumber = callNumber;
    checkedOut = new TreeMap<>();
  }

  public String getTitle() {
    return title;
  }

  public String getAuthor() {
    return author;
  }

  public String getCallNumber() {
    return callNumber;
  }

  public boolean isCheckedOut(){
    if (checkedOut.isEmpty()) {
      return false;
    }
      return true;
  }

  public boolean addCheckedOut(String borrowerEmail, CheckedOut newCheckedOut){
    checkedOut.put(borrowerEmail, newCheckedOut);
    return true;
  }

  public CheckedOut getCheckedOut(String borrowerEmail){
    return checkedOut.get(borrowerEmail);
  }

  public boolean removeCheckedOut(String borrowerEmail){
    checkedOut.remove(borrowerEmail);
    return true;
  }
}
