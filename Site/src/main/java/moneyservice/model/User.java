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
		final int minAmount = 5;
		Random rd = new Random();
		
		// selects a random currency ticker from the Currencies Map
		List<String> currencyKeys = new ArrayList<>(Configuration.currencies.keySet());
		String currencyTicker = currencyKeys.get(rd.nextInt(currencyKeys.size()));

		//Randomizes a amount within the range of the specific currency
		Double currencyMaxAmount = Configuration.boxOfCash.get(currencyTicker);
		int amount = (int)Math.floor(Math.random() * (currencyMaxAmount - minAmount + 1) + minAmount) % 10000;

		//Randomizes a TransactionMode either buy or sell.
		int mode = new Random().nextInt(TransactionMode.values().length);
		TransactionMode  transMode = TransactionMode.values()[mode];

		//Order creation based on randomized data.
		Order test = new Order(Site,currencyTicker,amount,transMode);
		System.out.println(test);
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
