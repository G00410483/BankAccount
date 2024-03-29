import java.io.*;
import java.net.*;
import java.util.Scanner;

// Class Requester handles client-side network communication with a server.
public class Requester {
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	String response;
	Scanner input;
	
	// Constructor initializes the Scanner for user input.
	Requester() {
		input = new Scanner(System.in);
	}
	
	// The main method for establishing a connection and handling user interactions.
	void run() {
		try {
			// 1. creating a socket to connect to the server
			requestSocket = new Socket("127.0.0.1", 2004);
			System.out.println("Connected to localhost in port 2004");
			// 2. get Input and Output streams for communication with the server
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			// 3: Communicating with the server
			// Handling client communication with the servers
			try {
				// Communicate with the server in a loop until the user decides to exit
				do {
					// Read registration or login message
					message = (String) in.readObject();
					System.out.println(message);
					response = input.next();
					sendMessage(response);
					
					// Handle registration process
					if (response.equalsIgnoreCase("1")) {
						handleRegistration();
					} 
					// Handle login process
					else if (response.equalsIgnoreCase("2")) {
						handleLogin();
					}
					// Option to close the connection
					else if (response.equalsIgnoreCase("0")) {
						// Close connection
					}
					else {
						message = (String) in.readObject();
						System.out.println(message);
					}
					
					// If user wants to login or exit
					// Break the loop if user input is not 1
					message = (String) in.readObject();
					System.out.println(message);
					response = input.next();
					sendMessage(response);
				
				} while (response.equalsIgnoreCase("1"));
				
				 // Logout if the response is not "1"
				if(!response.equalsIgnoreCase("1")) {
					logout();
				}

			} 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// Handles the registration process by interacting with the user and sending data to the server.
	private void handleRegistration() throws IOException, ClassNotFoundException {
		String name, ppsNum, mail, password, address, balance;

		// User inputing user data
		// User- name, PPS number, email address, password, address, and balance
		message = (String)in.readObject();
		System.out.println(message);
		input.nextLine(); // Consume the newline character
		name = input.nextLine();
		sendMessage(name);
		
		// Loop for PPS number validation
		do {
			message = (String)in.readObject();
			System.out.println(message);
			ppsNum = input.next();
			sendMessage(ppsNum);
			
			// Check if ppsNum has a length of 9, and the first 7 characters are digits,
            // and the last 2 characters are letters
            if (ppsNum.length() == 9 &&
                ppsNum.substring(0, 7).matches("\\d{7}") &&
                ppsNum.substring(7).matches("[a-zA-Z]{2}")) {
                break; // Exit the loop if the condition is met
            }
            else {
            	message = (String) in.readObject();
				System.out.println(message);
            }
		} while(true);
		
		// Loop for email validation
		do {
			message = (String)in.readObject();
			System.out.println(message);
			mail = input.next();
			sendMessage(mail);
			
			// Email validation: contains "@" and "."
			if(mail.contains("@") && 
				mail.contains(".") && 
				mail.contains("com")) {
				break; // Exit loop if valid
			}
			else {
				message = (String) in.readObject();
				System.out.println(message);
			}
		} while(true);
		
		// Reading and sending other user data
		// Password
		message = (String)in.readObject();
		System.out.println(message);
		password = input.next();
		sendMessage(password);
		// Address
		message = (String)in.readObject();
		System.out.println(message);
		input.nextLine(); // Consume the newline character
		address = input.nextLine();
		sendMessage(address);
		// Balance
		message = (String)in.readObject();
		System.out.println(message);
		balance = input.next();
		sendMessage(balance);
		// Display if registration was was successful
		message = (String)in.readObject();
		System.out.println(message);
	}
	
	// Handles the login process by interacting with the user and sending login credentials to the server.
	private void handleLogin() throws ClassNotFoundException, IOException {
		String validLogin;
		String exit = "";
		do {
			// Read the email input 
			message = (String)in.readObject();
			System.out.println(message);
			String email = input.next();
			sendMessage(email);

			// Read the password input
			message = (String)in.readObject();
			System.out.println(message);
			String pass = input.next();
			sendMessage(pass);
			
			// Check if login was successful
			validLogin = (String)in.readObject();
			System.out.println(validLogin);
			
			// If login was successful 
			if(!validLogin.equalsIgnoreCase("Invalid email or password.")) {
				handleUserChoice();
			}
			// If user input is -1 break the loop
			message = (String)in.readObject();
			System.out.println(message);
			exit = input.next();
			sendMessage(exit);

		}while(!exit.equalsIgnoreCase("-1"));
		logout();
	}
	// Handles user's choice after successful login, allowing access to different functionalities.
	private void handleUserChoice() throws ClassNotFoundException, IOException {
		boolean valid = true;;
		try {
			// Loop until user input is not 8
			do {
				// User inputs choice
				message = (String) in.readObject();
				System.out.println(message);
				response = input.next();
				sendMessage(response);

				switch (response) {
				case "3":
					handleLodgeMoney();
					break;
				case "4":
					handleRetrieveAllUsers();
					break;
				case "5":
					handleTransferMoney();
					break;
				case "6":
					handleViewTransactions();
					break;
				case "7":
					handlePasswordChange();
					break;
				case "8":
					logout();
					break;
				default:
					message = (String) in.readObject();
					System.out.println(message);
					valid = false;
				}

			} while (!response.equalsIgnoreCase("8") || valid == false);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	// Retrieves and displays all registered users from the server.
	private void handleRetrieveAllUsers() throws IOException, ClassNotFoundException {
		// Display "Total registered users: " string
		message = (String) in.readObject();
		System.out.println(message);

		// Read the total number of registered users and display it
		message = (String) in.readObject();
		System.out.println(message);
		int totalUsers = Integer.parseInt(message);

		try {
			// Read and display user information
			for (int i = 0; i < totalUsers; i++) {
				message = (String) in.readObject();
				System.out.println(message);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	// Handles the process of lodging (depositing) money into the user's account.
	private void handleLodgeMoney() throws IOException, ClassNotFoundException {
		// User input amount of money that will be lodged
		message = (String)in.readObject();
		System.out.println(message);
		String amount = input.next();
		sendMessage(amount);
		
		// Display message if payment was successful or not
		message = (String)in.readObject();
		System.out.println(message);
	}
	
	// Handles the process of transferring money to another user's account.
	private void handleTransferMoney() throws IOException, ClassNotFoundException {
		// Prompt the user to enter the recipient's email
		message = (String)in.readObject();
		System.out.println(message);
		String recipientMail = input.next();
		sendMessage(recipientMail);
		
		  // Prompt the user to enter the recipient's PPS number
		message = (String)in.readObject();
		System.out.println(message);
		String recipientPps = input.next();
		sendMessage(recipientPps);
		
		// Check if the user needs to enter the amount to transfer
		message = (String)in.readObject();
		System.out.println(message);
		if(message.equals("Please enter the amount to transfer: ")) {
			// Prompt the user to enter the amount to transfer
			String amount = input.next();
			sendMessage(amount);
			
			// Display a message indicating whether the transfer was successful or not
			message = (String)in.readObject();
			System.out.println(message);
		}
		else {
			// Display a message indicating the recipient's details and allow the user to confirm
			message = (String)in.readObject();
			System.out.println(message);
		}
	}
	// Handles the process of changing the user's password.
	private void handlePasswordChange() throws IOException, ClassNotFoundException {
		// Prompt the user to enter the new password
		message = (String)in.readObject();
		System.out.println(message);
		String newPass = input.next();
		sendMessage(newPass);
		
		// Display a message indicating whether the password change was successful or not
		message = (String)in.readObject();
		System.out.println(message);
	}
	
	// Handles the process of viewing the user's transaction history.
	private void handleViewTransactions() throws IOException, ClassNotFoundException {
		// Display a message indicating that the transactions are being retrieved
		message = (String)in.readObject();
		System.out.println(message);
		
		// Read the number of transactions
		message = (String)in.readObject();
		int size = Integer.parseInt(message);
		
		// Loop to receive and display each transaction
		for(int i = 0; i < size; i++) {
			message = (String)in.readObject();
			System.out.println(message);
		}
	}
	// Handles the logout process
	private void logout() throws IOException, ClassNotFoundException {
		// Display a logout message
		message = (String)in.readObject();
		System.out.println(message);
		
		// Close the input and output streams, and the socket
        in.close();
        out.close();
        requestSocket.close();
	}
	
	// Sends a message to the server
	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	// The main method for starting the client.
	public static void main(String args[]) {
		Requester client = new Requester();
		client.run();
	}
}
