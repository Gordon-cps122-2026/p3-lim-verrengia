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

  public Book(String title, String author, String callNumber, int copy) {
    this.title = title;
    this.author = author;
    this.callNumber = callNumber;
    checkedOut = new TreeMap<>();
    this.copy = copy;
    this.key = callNumber + Integer.toString(copy);
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

  public int getCopy(){
    return copy;
  }

  public String getKey(){
    return key;
  }

  public boolean isCheckedOut(){
    if (checkedOut.isEmpty()) {
      return false;
    }
      return true;
  }

  public boolean isNull(){
    return title.isEmpty()||title==null||author.isEmpty()||author==null||callNumber.isEmpty()||callNumber==null||copy==0;
  }

  public boolean addCheckedOut(String borrowerEmail, CheckedOut newCheckedOut){
    checkedOut.put(borrowerEmail, newCheckedOut);
    return true;
  }

  public CheckedOut getCheckedOut(){
    for(CheckedOut result : checkedOut.values()){
      if(result.getBook()==this){
        return result;
      }
    }
    return null;
  }

  public boolean removeCheckedOut(String borrowerEmail){
    checkedOut.remove(borrowerEmail);
    return true;
  }
}
