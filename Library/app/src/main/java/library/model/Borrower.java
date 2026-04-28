package library.model;

import java.util.*;

public class Borrower implements java.io.Serializable {
  // Required for serialization
  private static final long serialVersionUID = 1L;

  private String firstName;
  private String lastName;
  private String email;
  private String phone;
  private TreeMap<String, CheckedOut> checkedOut;

  public Borrower(String firstName, String lastName, String email, String phone) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phone = phone;
    this.checkedOut = new TreeMap<>();
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public boolean addCheckedOut(String callNumber, CheckedOut newCheckedOut) {
    checkedOut.put(callNumber, newCheckedOut);
    return true;
  }

  public CheckedOut getCheckedOut(String callNumber) {
    return checkedOut.get(callNumber);
  }

  public boolean removeCheckedOut(String callNumber) {
    checkedOut.remove(callNumber);
    return true;
  }

  public boolean hasCheckedOut() {
    if (checkedOut.isEmpty()) {
      return false;
    }
    return true;
  }

  public Collection<Book> getAllBooks() {
    Collection<Book> result = new ArrayList<>();
    
    if (hasCheckedOut()) {
      for (CheckedOut i : checkedOut.values()) {
        result.add(i.getBook());
      }
    }
    return result;
  }
}
