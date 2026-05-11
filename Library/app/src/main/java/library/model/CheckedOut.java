package library.model;

import java.io.Serializable;
import java.time.LocalDate;

/** Checkout record persisted inside {@link Book} / {@link Borrower} via Java serialization. */
public class CheckedOut implements Serializable {

  private static final long serialVersionUID = 1L;

  private Book book;
  private Borrower borrower;
  private LocalDate dueDate;
  private boolean renewed;

  /**
   * Creates a CheckedOut record associating a book with a borrower. The due date is automatically
   * set to 28 days from the current date.
   *
   * @param book The book being checked out
   * @param borrower The borrower checking out the book
   */
  public CheckedOut(Book book, Borrower borrower) {
    this.book = book;
    this.borrower = borrower;
    this.renewed = false;

    this.dueDate = LocalDate.now().plusDays(28);
  }

  /**
   * Gets the book associated with this checkout record.
   *
   * @return the book being checked out
   */
  public Book getBook() {
    return book;
  }

  /**
   * Gets the borrower associated with this checkout record.
   *
   * @return the borrower who checked out the book
   */
  public Borrower getBorrower() {
    return borrower;
  }

  /**
   * Gets the due date of this checkout.
   *
   * @return the date by which the book must be returned
   */
  public LocalDate getDueDate() {
    return dueDate;
  }

  /**
   * Checks whether this loan has already been renewed.
   *
   * @return true if the loan has been renewed, false otherwise
   */
  public boolean isRenewed() {
    return renewed;
  }

  /**
   * Renews the due date of this checkout by 28 days. A loan can only be renewed once.
   *
   * @return true if the renewal was successful, false if the loan has already been renewed
   */
  public boolean renewDueDate() {
    if (renewed) {
      return false;
    }
    dueDate = dueDate.plusDays(28);
    renewed = true;

    return true;
  }
}
