package library.gui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import library.model.Book;
import library.model.Borrower;
import library.model.LibraryDatabase;

/**
 * Read-only access to library maps for building tables. Uses reflection so the model package stays
 * unchanged.
 */
final class DatabaseView {

  private DatabaseView() {}

  @SuppressWarnings("unchecked")
  static TreeMap<String, Book> books(LibraryDatabase db) {
    try {
      Field f = LibraryDatabase.class.getDeclaredField("books");
      f.setAccessible(true);
      return (TreeMap<String, Book>) f.get(db);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  @SuppressWarnings("unchecked")
  static TreeMap<String, Borrower> borrowers(LibraryDatabase db) {
    try {
      Field f = LibraryDatabase.class.getDeclaredField("borrowers");
      f.setAccessible(true);
      return (TreeMap<String, Borrower>) f.get(db);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  static List<Map.Entry<String, Book>> bookEntries(LibraryDatabase db) {
    return new ArrayList<>(books(db).entrySet());
  }

  static List<Map.Entry<String, Borrower>> borrowerEntries(LibraryDatabase db) {
    return new ArrayList<>(borrowers(db).entrySet());
  }
}
