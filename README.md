Save the prompts here.

Prompt 1:
Please help me create a UI in InventoryUI.java that fits the following description:

When the UI is started up a window with a frame title "Library App" at the top. There are two tabs at the top called "Books" and "Borrowers".

By default the Books tab is open and in a textbox it lists each book in a separate row, including its title, then author, then call number. The user should not be able to edit this. This text box should be internally called "bookListArea". The page also has a button on the bottom labeled "Add Book"

When the Borrowers tab is clicked a page similar to the books page appears, showing a textbox titled "Borrower Collection" listing each borrower in a separate row, including their first name, last name, email, and phone number which the user cannot edit from here, this text box should be internally called "borrowersListArea". This page also has a button at the bottom labeled "Add Borrower" 

Now, if the "Add Book" button is clicked, the frame shows 3 labeled text fields allowing the user to input a title, author, then call number. This should have a title "New Book" above the fields. At the bottom are two buttons called "Back to Collection" which leads back to the beginning page and "Add Book" which uses the information from the text field (internally called titleText, authorText, and callNumberText") to create a new book object which is added to the book collection and displayed on the beginning page. 

When the "Add Borrower" tab is clicked a similar frame display appears where the frame shows 4 labeled text fields allowing the user to input a first name, last name. email address, and phone number. This should have a title "New Borrower" above the fields. At the bottom are two buttons called "Back to Collection" and "Add Borrower" which uses the information from the text field (internally called firstNameText, lastNameText, emailText, and phoneText")to create a new borrower object which is added to the borrower collection and displayed on the beginning page.

