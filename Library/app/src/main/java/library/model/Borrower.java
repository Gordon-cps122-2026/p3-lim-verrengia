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

  public boolean addCheckedOut(String callNum, CheckedOut newCheckedOut){
    checkedOut.put(callNum, newCheckedOut);
    return true;
  }

  public CheckedOut getCheckedOut(String callNum){
    return checkedOut.get(callNum);
  }

  public boolean removeCheckedOut(String callNum){
    checkedOut.remove(callNum);
    return true;
  }
}
