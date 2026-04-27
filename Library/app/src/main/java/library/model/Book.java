package library.model;

import java.util.TreeMap;

public class Book implements java.io.Serializable {

  //TODO make a system that can check if the due date is expired

  private String title;
  private String author;
  private String callNumber;
  private static final long serialVersionUID = 1L;
  private CheckedOut checkedOut;

  public Book(String title, String author, String callNumber) {
    this.title = title;
    this.author = author;
    this.callNumber = callNumber;
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
    if (checkedOut == null) {
      return true;
    }
      return false;
  }

  public boolean addCheckedOut(String callNumber, CheckedOut newCheckedOut){
    checkedOut = newCheckedOut;
    return true;
  }

  public CheckedOut getCheckedOut(String callNumber){
    return checkedOut;
  }

  public boolean removeCheckedOut(String callNumber){
    checkedOut = null;
    return true;
  }
}
