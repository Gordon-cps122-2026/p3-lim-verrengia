Save the prompts here.

(Discarded) Prompt 1:
Please help me create a UI in InventoryUI.java that fits the following description:

When the UI is started up a window with a frame title "Library App" at the top. There are two tabs at the top called "Books" and "Borrowers".

By default the Books tab is open and in a textbox it lists each book in a separate row, including its title, then author, then call number. The user should not be able to edit this. This text box should be internally called "bookListArea". The page also has a button on the bottom labeled "Add Book"

When the Borrowers tab is clicked a page similar to the books page appears, showing a textbox titled "Borrower Collection" listing each borrower in a separate row, including their first name, last name, email, and phone number which the user cannot edit from here, this text box should be internally called "borrowersListArea". This page also has a button at the bottom labeled "Add Borrower" 

Now, if the "Add Book" button is clicked, the frame shows 3 labeled text fields allowing the user to input a title, author, then call number. This should have a title "New Book" above the fields. At the bottom are two buttons called "Back to Collection" which leads back to the beginning page and "Add Book" which uses the information from the text field (internally called titleText, authorText, and callNumberText") to create a new book object which is added to the book collection and displayed on the beginning page. 

When the "Add Borrower" tab is clicked a similar frame display appears where the frame shows 4 labeled text fields allowing the user to input a first name, last name. email address, and phone number. This should have a title "New Borrower" above the fields. At the bottom are two buttons called "Back to Collection" and "Add Borrower" which uses the information from the text field (internally called firstNameText, lastNameText, emailText, and phoneText")to create a new borrower object which is added to the borrower collection and displayed on the beginning page.




Prompt used to create LibraryGUI:
Key LibraryDatabase methods I need you to use: 
- addBook(String title, String author, String callNumber) - boolean 
- addBorrower(String firstName, String lastName, String email, String phone) - boolean 
- getBookCsv() - String (format: “title”,”author”,”callNumber” per line) 
- getBorrowerCsv() - String (format: “firstName”,”lastName”,”email”,”phone” per line) 
- getInstance() - LibraryDatabase 

Create a new file: library/gui/LibraryGUI.java 
Do NOT modify any existing files. 

GUI Requirements:

Main Window 
- Single window, titled “Library App” 
- three panels: “home”, “addBook”, “addBorrower” 
- On startup, show the “home” panel 

Home Panel 
- A JTabbedPane with two tabs: “Books” and “Borrowers” 
- Books tab: 
    - header: “Collection” 
    - text field named `bookListArea`, non-editable
    - “Add Book” button at bottom-right 
- Borrowers tab: 
    - header: “Borrowers” 
    - text field named `borrowerListArea`, non-editable
    - “Add Borrower” button at bottom-right 

Book Panel 
- header: “New Book” 
- Three right-aligned JLabel + JTextField rows: 
    - “Call Number:” - JTextField named `callNumberText` 
    - “Title:” - JTextField named `titleText` 
    - “Author:” - JTextField named `authorText` 
- Bottom row: 
    - Left: JButton “Back to Collection” - switches CardLayout to “home”, selects Books tab 
    - Right: JButton “Add Book” (blue background, white text) - used to add book 
        - calls writeToFile(), refreshes bookListArea, clears fields, switches to “home” with Books tab selected
        - if error: shows JOptionPane warning “Book already exists or fields are empty” 

Borrower Panel 
- JLabel header: “New Borrower” 
- Four right-aligned JLabel + JTextField rows: 
    - “First Name:” - JTextField named `firstNameText` 
    - “Last Name:” - JTextField named `lastNameText` 
    - “Email:” - JTextField named `emailText` 
    - “Phone:” - JTextField named `phoneText` 
- Bottom row: 
    - Left: “Back to Borrowers” button - switches CardLayout to “home”, selects Borrowers tab 
    - Right: “Add Borrower” button (blue background, white text) - used to add borrower
        - calls writeToFile(), refreshes borrowerListArea, clears fields, switches to “home” with Borrowers tab selected  
        - if error: shows JOptionPane warning “Borrower already exists or fields are empty” 

- labels should be right-aligned and text fields stretch to fill width 
- refreshBookList() sets bookListArea.setText(db.getBookCsv()) 
- refreshBorrowerList() sets borrowerListArea.setText(db.getBorrowerCsv()) 
- Call both refresh methods on startup so any previously saved data is shown 
- Use only standart java fuctions




following prompt:

tried to run the code, but it prints 
Error: Could not find or load main class library.gui.LibraryGUI
Caused by: java.lang.ClassNotFoundException: library.gui.LibraryGUI

read whole code and modify it so that it runs properly. Do NOT modity files in model folder.




Prompt used to separate LibraryGUI into BooksTab, BorrowersTab, and MainWindow:
modify the code to pass the tests in BooksTapTest.java, BorrowersTapTest.java, and MainWindowTest.java.
read all the test files first. Then modify LibraryGUI.java so that every test passes.

Split LibraryGUI.java into separate files based on what each test expects. The overall window appearance and function must remain the same. Preserve all behaviors in the LibraryGUI.java.
- BooksTab.java
	- contains: Add Book button, Back to Borrowers button
- BorrowersTab.java
    - contains: Add Borrower button, Back to Borrowers button
- MainWindow.java
	- holds reference to the BooksTab and BorrowersTab panels

Do NOT modify files in model folder.
Do NOT modiry test files.




following prompts:
Refactor the project by distributing all code from LibraryGUI.java into MainWindow.java, BooksTab.java, and BorrowersTab.java, and then delete LibraryGUI.java.
Read LibraryGUI.java, Read MainWindow.java, BooksTab.java, and BorrowersTab.java before touching anything.

MainWindow.java
  - basic window setup (title, size, close operation)
  - Reference to Books and Borrowers tabs
  - Any top-level navigation logic (switching between cards/tabs)

BooksTab.java
  - Books tab panel and Add Book panel
  - All components: bookListArea, callNumberText, titleText, authorText
  - Add Book button and Back to Collection button logic
  - refreshBookList() method

BorrowersTab.java
  - Borrowers tab panel and Add Borrower panel
  - All components: borrowerListArea, firstNameText, lastNameText, emailText, phoneText
  - Add Borrower button and Back to Borrowers button logic
  - refreshBorrowerList() method

  - All behavior must work the same.
  - Do NOT modify files in model folder.
  - Do NOT modiry test files.
  - Once all code is confirmed to be moved, delete LibraryGUI.java
  - If a main() method is needed, it should live in MainWindow.java


