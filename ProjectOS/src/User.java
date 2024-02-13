
import java.util.LinkedList;
import java.util.List;

public class User {
	
	// Declare private instance variables
	private String name;
	private String ppsNum;
	private String mail;
	private String pass;
	private String address;
	private float balance;
	private List<String> transactions;
	
	// Constructor to initialize the User object with given parameters
	public User(String name, String ppsNum, String mail, String pass, String address, float balance) {
		this.name = name;
		this.ppsNum = ppsNum;
		this.mail = mail;
		this.pass = pass;
		this.address = address;
		this.balance = balance;
		// Initialize the list of transactions as a LinkedList
		transactions = new LinkedList<>();
	}
	
	// Getter methods to retrieve the user data
	public String getMail() {
		return mail;
	}
	public float getBalance() {
		return balance;
	}
	public String getName() {
		return name;
	}
	public Object getPpsNum() {
		return ppsNum;
	}
	public Object getPassword() {
		return pass;
	}

	public Object getAddress() {
		return address;
	}
	
	// Setter methods
	public void setBalance(float f) {
		this.balance = f;
	}
	public void setPassword(String newPassword) {
		this.pass = newPassword;
	}
	
	// Method to authenticate the user based on the provided password
	public boolean authenticate(String password) {
		return pass.equals(password);
	}
	
	// Getter method to retrieve a copy of the list of transactions
	public List<String> getTransactions() {
		return new LinkedList<>(transactions);
	}
	
	// Method to add transactions to the list
	public void addTransaction(String transaction) {
		transactions.add(transaction);
	}
	// Override the toString() method to provide a custom string representation of the object
	@Override
	public String toString() {
		return String.format("Name: %s, PPS Number: %s, Email: %s, Address: %s, Balance: %.2f",
				name.toUpperCase(), ppsNum, mail, address, balance);
	}


}
