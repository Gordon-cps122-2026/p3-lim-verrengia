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

  public boolean addCheckedOut(String email, CheckedOut newCheckedOut){
    checkedOut.put(email, newCheckedOut);
    return true;
  }

  public CheckedOut getCheckedOut(String email){
    return checkedOut.get(email);
  }

  public boolean removeCheckedOut(String email){
    checkedOut.remove(email);
    return true;
  }

  public boolean hasCheckedOut() {
    if (checkedOut.isEmpty()) {
      return false;
    }
    return true;
  }
}