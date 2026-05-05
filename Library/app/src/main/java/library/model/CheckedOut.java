package library.model;

import java.time.LocalDate;
import java.util.*;

public class CheckedOut {

  // TODO make a system that can check if the due date is expired

  private Book book;
  private Borrower borrower;
  private LocalDate dueDate;
  private boolean renewed;

  public CheckedOut(Book book, Borrower borrower) {
    this.book = book;
    this.borrower = borrower;
    this.renewed = false;

    this.dueDate = LocalDate.now().plusDays(28);
  }

  public Book getBook() {
    return book;
  }

  public Borrower getBorrower() {
    return borrower;
  }

  public LocalDate getDueDate() {
    return dueDate;
  }

  public boolean isRenewed() {
    return renewed;
  }

  public boolean renewDueDate() {
    if (renewed) {
      return false;
    }
    dueDate = dueDate.plusDays(28);
    renewed = true;

    return true;
  }

}
