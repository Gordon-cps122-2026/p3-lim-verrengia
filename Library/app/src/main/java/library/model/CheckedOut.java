package library.model;

import java.util.*;

public class CheckedOut {

  //TODO make a system that can check if the due date is expired

  private Book book;
  private Borrower borrower;
  private Calendar dueDate;
  private boolean renewed;

  public CheckedOut(Book book, Borrower borrower) {
    this.book = book;
    this.borrower = borrower;
    this.renewed = false;

    Calendar now = Calendar.getInstance();
    now.add(Calendar.DAY_OF_MONTH, 28);
    this.dueDate = now;
  }

  public Book getBook() {
    return book;
  }

  public Borrower getBorrower() {
    return borrower;
  }

  public String getDueDate() {
    return dueDate.get(Calendar.MONTH) + 1
        + "/"
        + dueDate.get(Calendar.DAY_OF_MONTH)
        + " "
        + dueDate.get(Calendar.YEAR);
  }

  public boolean isRenewed() {
    return renewed;
  }

  public boolean renewDueDate() {
    if (renewed == true) {
        return false;
    }

    dueDate.add(Calendar.DAY_OF_MONTH, 28);
    renewed = true;

    return true;
  }
}
