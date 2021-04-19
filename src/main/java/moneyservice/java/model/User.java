package moneyservice.java.model;

public class User {
	
	// ATTRIBUTES
	
	private final String name;
	
	
	// DEFAULT CONSTRUCTOR
	
	public User(String name) {
		this.name = name;
	}
	
	
	// METHODS
	
	public Order createOrderRequest() {
		TransactionMode transactionMode = null;
		Order test = new Order("Test", "SEK", 1000, transactionMode.BUY);
		return test;
	}

	// GETTERS AND SETTERS

	public String getName() {
		return name;
	}
	
	
	// TOSTRING ??
	
}
