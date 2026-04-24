package library.model;


public class Book implements java.io.Serializable {

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
    Borrower borrower = checkedOut.getBorrower();
    if(borrower==null){
      return false;
    }
    else{
      return true;
    }
  }

  public CheckedOut getCheckedOut(){
    return checkedOut;
  }

  public boolean setCheckedOut(String callNumber){
    checkedOut = CheckedOut(callNumber,email);
    return true;
  }

  public boolean removeCheckedOut(){
    checkedOut = CheckedOut(null,null);
    return true;
  }
}
