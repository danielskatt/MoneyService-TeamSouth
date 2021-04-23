package moneyservice.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
	public Optional<Order> createOrderRequest() { //first draft
		final String Site = "South";
		Random rd = new Random();
		int [] bills = {50, 100, 200, 500, 1000};

		// Selects a random currency ticker from the Currencies Map
		List<String> currencyKeys = new ArrayList<>(Configuration.currencies.keySet());
		String currencyTicker = currencyKeys.get(rd.nextInt(currencyKeys.size())); 

		//Randomizes a amount within the range of the bills array
		int amount = bills[rd.nextInt(bills.length)];
		
		//Randomizes a TransactionMode either buy or sell.
		int mode = new Random().nextInt(TransactionMode.values().length);
		TransactionMode  transMode = TransactionMode.values()[mode];

		//Order creation based on randomized data.
		Order test = new Order(Site,currencyTicker,amount,transMode);
		System.out.println(test); // For display purposes only, will be removed later on.
		Optional<Order> theOrder= Optional.of(test);
		
		return theOrder;
	}

	/**
	 * Method that will return the name of the user.
	 * @return name - name of the user.
	 */
	public String getName() {
		return name;
	}
	
}
