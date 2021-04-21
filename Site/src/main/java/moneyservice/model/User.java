package moneyservice.model;

/**---------------User Class-------------------
 * Holds information about a specific user such as the name.
 * Also allows the specific user to create order requests.
 */
public class User {
	
	/**
	 * @attribute name - holds information about the name of the user.
	 */
	private final String name;
	
	/**
	 * Constructor for User class.
	 * @param name - the users name.
	 */
	public User(String name) {
		this.name = name;
	}
	
	/** 
	 * Method that will create an order request based on a few parameters.
	 * @return An order.
	 */
	public Order createOrderRequest() {
		TransactionMode transactionMode = null;
		Order test = new Order("Test", "SEK", 1000, transactionMode.BUY);
		return test;
	}

	/**
	 * Method that will return the name of the user.
	 * @return name - name of the user.
	 */
	public String getName() {
		return name;
	}
	
}
