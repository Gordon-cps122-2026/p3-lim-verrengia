package library.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import library.model.Book;
import library.model.Borrower;
import library.model.LibraryDatabase;

/** UI-layer helpers for checkout/return using public database APIs only. */
final class CirculationHelper {

  private CirculationHelper() {}

  static List<String> candidateKeys(String call) {
    String c = call.trim();
    List<String> keys = new ArrayList<>();
    keys.add(c);
    for (int i = 2; i <= 30; i++) {
      keys.add(c + i);
    }
    return keys;
  }

  /** @return "ok" or error message */
  static String tryCheckout(LibraryDatabase db, String email, String call) {
    if (!db.getEmails().contains(email)) {
      return "Borrower not found";
    }
    boolean anyKey = false;
    for (String k : candidateKeys(call)) {
      if (DatabaseView.books(db).containsKey(k)) {
        anyKey = true;
        break;
      }
    }
    if (!anyKey) {
      return "Book not found";
    }
    for (String k : candidateKeys(call)) {
      if (!DatabaseView.books(db).containsKey(k)) {
        continue;
      }
      if (db.checkout(k, email)) {
        return "ok";
      }
    }
    return "Already checked out";
  }

  static boolean tryReturn(LibraryDatabase db, String email, String call) {
    String c = call.trim();
    for (Map.Entry<String, Book> e : DatabaseView.bookEntries(db)) {
      Book b = e.getValue();
      if (!b.getCallNumber().equals(c)) {
        continue;
      }
      if (!b.isCheckedOut()) {
        continue;
      }
      Borrower br = b.getCheckedOut().getBorrower();
      if (!br.getEmail().equalsIgnoreCase(email.trim())) {
        continue;
      }
      return db.returnBook(e.getKey());
    }
    return false;
  }
}
