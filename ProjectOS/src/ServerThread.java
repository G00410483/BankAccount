import java.io.*;
import java.net.Socket;
import java.util.List;

/**
* Represents a server thread responsible for handling communication and interactions with a connected client.
* This class manages a socket connection, input/output streams for communication, and user-related operations
* such as registration, login, money transfer, and more.
* Some parts of code were referenced from: 
	* https://www.w3resource.com/java-exercises/thread/java-thread-exercise-7.php
	* https://stackoverflow.com/questions/52957047/multithreading-bank-account-java
*/
public class ServerThread extends Thread {
	
	// Represents socket connection
    private Socket myConnection;
    // For reading objects from the client
    private ObjectInputStream in;
    // For writing objects to the client
    private ObjectOutputStream out;
    // Library object that contains book data
    private Library myLib;
    // Current user associated with this thread
    private User currentUser;
    
    // Constructor for ServeThread class that takes a Socket and Library object as parameters
    public ServerThread(Socket socket, Library library) {
    	// Initialize the variables
        myConnection = socket;
        myLib = library;
    }
    
    /**
     * Handles communication and interaction with a connected client.
     * This method sets up input and output streams for communication with the client,
     * presents a menu of options, processes user choices, and handles the registration
     * and login of users. It repeats the process based on the user's choice and closes
     * the connection when done.
     */
    public void run() {
    	// Initialize variable for repeating the loop
    	String repeat;
    	
        try {
        	// Set up ObjectInput/OutputStream for communication
            out = new ObjectOutputStream(myConnection.getOutputStream());
            out.flush();
            in = new ObjectInputStream(myConnection.getInputStream());

            // Server Comms
            do {
                sendMessage("Please enter one of the following options:\n1. REGISTER\n2. LOGIN\n0. EXIT");
                String choice = (String) in.readObject();
                System.out.println(choice);
                
                // Process the user choice
                switch (choice) {
                    case "1":
                        registerUser();
                        break;
                    case "2":
                        loginUser();
                        break;
                    case "0":
                        closeConnection();
                        return;  // Exit the loop and thread
                    default:
                        sendMessage("Invalid option. Please try again.");
                }
                // Ask user if they want to repeat the process
                // If input not 1 exit the loop
                sendMessage("Enter 1 to go back: ");
				repeat = (String)in.readObject();
				System.out.println(repeat);

            } while(repeat.equalsIgnoreCase("1"));   
        // Logout user
        logout();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
        	// Close the connection regardless of the outcome
            closeConnection();
        }
    }
    
    /**
     * Registers a new user by obtaining user information from the client, validating it,
     * and updating the library with the new user details.
     */
    private void registerUser() throws IOException, ClassNotFoundException {
    	
    	String name, ppsNum, mail, password, address, balance;
    	boolean validMail = false;
    	
    	// Prompt the user for their details- name, PPS number, email address, password, address and balance
    	sendMessage("Please enter your name: ");
        name = (String) in.readObject();
        
        // While loop for PPS number- until conditions are met
        do {
            sendMessage("Please enter your PPS number: ");
            ppsNum = (String) in.readObject();
            
            // Check if ppsNum has a length of 9, and the first 7 characters are digits,
            // and the last 2 characters are letters
            // Reference: https://stackoverflow.com/questions/14792063/regular-expression-for-7-digits-followed-by-an-optional-3-letters
            if (ppsNum.length() == 9 &&
                ppsNum.substring(0, 7).matches("\\d{7}") &&
                ppsNum.substring(7).matches("[a-zA-Z]{2}")) {
                break; // Exit the loop if the condition is met
            }
            else {
                sendMessage("Invalid PPS number. It must be 9 characters with the "
                		+ "first seven digits the last two being letters.");
            }
        } while (true); // Infinite loop until the condition is met
        
        // While loop for e-mail address- until conditions are not met
        do {
        	sendMessage("Please enter your e-mail address: ");
            mail = (String) in.readObject();
            
            // Check if e-mail address contains '@', '.' and 'com'
            if(mail.contains("@") && 
    				mail.contains(".") && 
    				mail.contains("com")) {
    				break;
    			}
    			else {
    				sendMessage("Invalid Email Address, must contain '@', '.' and 'com'.");
    			}
            
        } while(!validMail); // Infinite loop until the condition is met
        
        sendMessage("Please enter your password: ");
        password = (String) in.readObject();

        sendMessage("Please enter your address: ");
        address = (String) in.readObject();

        sendMessage("Please enter your current balance: ");
        balance = (String) in.readObject();
        
        // Add the new user to the library if the PPS number or email address provided are unique
        if (myLib.addUser(name, ppsNum, mail, password, address, balance)) {
            sendMessage("Registration successful!");
        } 
        // If provided PPS number or email address are not unique
        else {
            sendMessage("User with the same PPS Number or Email already exists.");
        }
    }
    /**
     * Logs in a user by obtaining their email address and password, validating the credentials,
     * and allowing access to the user's account.
     * This method repeatedly prompts the user to enter their email address and password, checks
     * the provided credentials for validity, and proceeds to handle login options if successful.
     */
    private void loginUser() throws IOException, ClassNotFoundException {
    	String email, password, exit;
    	
    	do {
    		// Prompt the user to enter their email address
    		sendMessage("Please enter your e-mail address: ");
    		// Read the entered email address
	        email = (String) in.readObject();
	        
	        // Prompt the user to enter their password
	        sendMessage("Please enter your password: ");
	        // Read the entered password
	        password = (String) in.readObject();
	        
	        // Attempt to log in the user using entered email and password
	        currentUser = myLib.loginUser(email, password);
	        
	        // Check if login was successful
	        if (currentUser != null) {
	        	// If successful proceed to handle login options
	            sendMessage("Login successful. Welcome, " + currentUser.getName().toUpperCase());
	            handleLoggedInOptions();
	        } 
	        else {
	            sendMessage("Invalid email or password.");
	        }
	        sendMessage("Enter -1 to EXIT or any other key to CONTINUE: ");
	        exit = (String) in.readObject();
	        
    	} while(!exit.equalsIgnoreCase("-1"));
    	logout();
    }

    /**
     * Handles the menu of options for a logged-in user.
     * This method displays a menu of available options, reads the user's choice,
     * and performs actions based on the chosen option.
     */
    private void handleLoggedInOptions() throws IOException, ClassNotFoundException {
        boolean valid = true;
        String choice;
        
    	do {
        	// Display a menu of options for the logged-in user
            sendMessage("Please enter one of the following options:"
            		+ "\n3. Lodge money\n4. Retrieve all registered users listing\n" 
            		+"5. Transfer money\n6. View all transactions on your bank account\n7. Update your password\n8. Logout");
            
            // Read the user's choice from the input stream.
            choice = (String) in.readObject();
            
            // Use a switch statement to perform actions based on the user's choice
            switch (choice) {
                case "3":
                    lodgeMoney();
                    break;
                case "4":
                    retrieveAllUsers();
                    break;
                case "5":
                    transferMoney();
                    break;
                case "6":
                    viewTransactions();
                    break;
                case "7":
                    updatePassword();
                    break;
                case "8":
                    logout();
                    break;
                default:
                    sendMessage("Invalid option. Please try again.");
                    valid = false;
            }
        } while (!choice.equalsIgnoreCase("8") || valid == false);
    }
    
    /**
     * Allows the user to lodge money into their account
     * This method prompts the user to enter an amount to lodge, processes the input,
     * updates the user's balance, and saves the transaction and user balances to files.
     * Reference: https://codereview.stackexchange.com/questions/275456/atm-console-program-with-java
     */
    private void lodgeMoney() throws IOException, ClassNotFoundException {
    	// Send a message to prompt the user to enter the amount to lodge
    	sendMessage("Please enter the amount to lodge: ");
    	// Read the amount entered by the user as a string from input
        String amountStr = (String) in.readObject();

        try {
        	// Parse the entered amount string to a floating-point number
            float amount = Float.parseFloat(amountStr);
            // Update the user's balance by adding the lodged amount
            currentUser.setBalance(currentUser.getBalance() + amount);
            // Send a success message with the updated balance to the user
            sendMessage("Money lodged successfully. Updated balance: " + currentUser.getBalance());
            myLib.saveUsers();  // Save the updated user balances to file
            myLib.saveUserTransactions(currentUser);  // Save the user transactions to file
        } catch (NumberFormatException e) {
            sendMessage("Invalid amount. Please enter a valid number.");
        }
    }
    /**
     * Retrieves and sends information about all registered users to the client.
     * This method retrieves the list of all registered users, sends the total number
     * of users to the client, and then iterates through each user to send their
     * information to the client.
     */
    private void retrieveAllUsers() throws IOException {
    	// Retrieve the list of all registered users from the library
        List<User> allUsers = myLib.getUserList();

        // Send the total number of registered users
        sendMessage("Total registered users: ");
        sendMessage(Integer.toString(allUsers.size()));
        
        // Iterate through each user and send their information to the client
        for (User user : allUsers) {
            // Send user information to client
            sendMessage(user.toString());
        }
    }
    
    /**
     * Transfers money from the current user's account to another user's account.
     * This method prompts the user to enter the recipient's email and PPS number,
     * validates the recipient's identity, and then prompts for the amount to transfer.
     * If the recipient is valid and there are sufficient funds, the money is transferred,
     * and a success message is sent. Otherwise, appropriate error messages are sent.
     * Reference: https://stackoverflow.com/questions/57001044/how-to-transfer-funds-from-one-account-to-another-in-java-using-user-input-for
     */
    private void transferMoney() throws IOException, ClassNotFoundException {
    	// Prompt the user to enter the recipient's email
    	sendMessage("Please enter the recipient's email: ");
        String recipientEmail = (String) in.readObject();
        
        // Prompt the user to enter the recipient's PPS number
        sendMessage("Please enter the recipient's PPS number: ");
        String recipientPps = (String) in.readObject();
        
        // Find the recipient user based on their email
        User recipient = myLib.findUserByEmail(recipientEmail);
        
        // Check if the recipient exists and the PPS number matches
        if (recipient != null && recipient.getPpsNum().equals(recipientPps)) {
        	// Prompt the user to enter the amount to transfer
        	sendMessage("Please enter the amount to transfer: ");
            String amountStr = (String) in.readObject();
            float amount = Float.parseFloat(amountStr);
            
            // Attempt to transfer money from the current user to the recipient
            if (myLib.transferMoney(currentUser, recipient, amount)) {
                sendMessage("Money transferred successfully. Updated balance: " + currentUser.getBalance());
            } 
            else {
                sendMessage("Insufficient funds for the transfer.");
            }
        } else {
            sendMessage("Recipient not found.");
        }
    }
    
    /**
     * Displays the transaction history for the current user.
     * This method retrieves the transaction information for the current user from a library,
     * sends the total number of transactions and each transaction's details to the client.
     */
    private void viewTransactions() throws IOException {
        // Retrieve and send transaction information
        List<String> transactions = myLib.getUserTransactions(currentUser);
        // Send a message indicating the user whose transactions are being displayed
        sendMessage("Transactions for user " + currentUser.getName().toUpperCase() + ":");
        // Send the total number of transactions
        sendMessage(Integer.toString(transactions.size()));
        // Iterate through each transaction and send it to the client
        for (String transaction : transactions) {
            sendMessage(transaction);
        }
    }
    
    /**
     * Allows the user to update their password.
     * This method prompts the user to enter a new password, updates the user's password
     * in the system, and then saves the updated user information to a file.
     */
    private void updatePassword() throws IOException, ClassNotFoundException {
    	// Prompt the user to enter their new password
    	sendMessage("Please enter your new password: ");
    	// Read the new password entered by the user as a string from input
        String newPassword = (String) in.readObject();
        // Update the user's password with the new password
        currentUser.setPassword(newPassword);
        myLib.saveUsers(); // Save the updated user information, including the new password, to a file
        // Send a success message indicating that the password has been updated
        sendMessage("Password updated successfully.");
    }
    
    /**
     * Logs out the current user and closes the connection.
     * This method performs the logout operation by sending a logout success message
     * to the client, clearing the reference to the current user, and closing the connection.
     */
    private void logout() throws IOException {
    	// Send a logout success message to the client
        sendMessage("Logout successful.");
        // Clear the current user
        currentUser = null;
        // Close connection
        closeConnection();
    }
    
    /**
     * This method sends the provided message to the client.
     */
    private void sendMessage(String msg) throws IOException {
    	// Write the message to the output stream
    	out.writeObject(msg);
    	// Flush the output stream to ensure the message is sent immediately
        out.flush();
     // Print the message to the server's console for logging
        System.out.println("server > " + msg);
    }
    
    /**
     * Closes the connection with the client.
     */
    private void closeConnection() {
        try {
        	// Close the input stream
            in.close();
            // Close the output stream
            out.close();
            // Close the connection with the client
            myConnection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
