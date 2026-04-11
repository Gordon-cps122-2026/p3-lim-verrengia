package library.model;


public class Book implements java.io.Serializable {

  private String title;
  private String author;
  private String callNumber;
  private static final long serialVersionUID = 1L;

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
}
