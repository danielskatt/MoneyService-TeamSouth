package moneyservice.site.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Logger;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.TransactionMode;

/**
 * The User class holds information about a specific customer and allows the customer
 * to create an Order that gets processed by Money Service Site.
 */
public class User {

	/**
	 * logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}


	/**
	 * name a String holding the name of the user
	 */
	private final String name;


	/**
	 * Default constructor for creating a complete User object
	 * @param name a String holding the name of the user
	 */
	public User(String name) {
		this.name = name;
	}

	/**
	 * Gets unsigned number from user input. If entry is not valid return value equals to -1
	 * @return num an int {@code >=} 0 OR -1 if input is invalid
	 */
	private static int getInputUint() {
		@SuppressWarnings("resource")
		Scanner keyboard = new Scanner(System.in);
		int num;
		final int errorNo = -1;

		if(keyboard.hasNextInt()) {
			num = keyboard.nextInt();
			if(num < 0) {	// check if unsigned 
				System.out.println(num + " is not a valid number!");
				num = errorNo;
			}
			return num;
		}

		String input = keyboard.next();
		System.out.println(input + " is not a valid number!");
		return errorNo;
	}

	/**
	 * This method lets a user create an Order through CLI
	 * @return Optional Order created by a User
	 */
	public Optional<Order> userCreatedOrder(){

		TransactionMode orderTmode = null;	// Orders TransactionMode

			System.out.format("%n*** Create an order --------------------%n");
			System.out.println("Do you want to buy or sell currency?");
			System.out.println("1 - BUY");
			System.out.println("2 - SELL");

			int menuMin = 1, menuMax = 2;
			int userModeInput;
			do {
				System.out.format("%nEnter your choice (%d-%d): ", menuMin, menuMax);
				userModeInput = getInputUint();
				if(userModeInput> menuMax) {
					System.out.println(userModeInput + " is not a menu choice!");
				}	
			}while(!(userModeInput >= menuMin && userModeInput <= menuMax));

			switch(userModeInput) {
			case 1:		// BUY mode for user equals SELL mode for Site			
				orderTmode = TransactionMode.SELL;
				break;
			case 2:		// SELL mode for user equals BUY mode for Site	
				orderTmode = TransactionMode.BUY;
				break;
			}

			// get all available currency codes and assign them a number
			Map<Integer, String> currencyCodes = new TreeMap<>();
			Collection<String> tempCurrencyCodes = Configuration.getCurrencies().keySet();
			int numVal = 1;
			for(String currencyCode: tempCurrencyCodes) {
				currencyCodes.putIfAbsent(numVal++, currencyCode);
			}

			// present menu for currency codes
			System.out.format("%n*** Create an order --------------------%n");
			System.out.println("Available currencys: ");

			for(Map.Entry<Integer,String> entry : currencyCodes.entrySet()) {
				int key = entry.getKey();
				String value = entry.getValue();

				System.out.println(key + " - " + value);
			}
			
			// get user input
			int cCodeMenuMin = 1, cCodeMenuMax = currencyCodes.size();
			int userCcodeInput;
			do {
				System.out.format("%nEnter your choice (%d-%d): ", cCodeMenuMin, cCodeMenuMax);
				userCcodeInput = getInputUint();
				if(userCcodeInput> cCodeMenuMax) {
					System.out.println(userCcodeInput + " is not a menu choice!");
				}	
			}while(!(userCcodeInput >= cCodeMenuMin && userCcodeInput <= cCodeMenuMax));

			String orderCcode = currencyCodes.get(userCcodeInput);	// Orders currency code
			
			// present menu for amount input
			int [] bills = {50, 100, 200, 500, 1000};
			System.out.format("%n*** Create an order --------------------%n");
			System.out.println("Available bills: ");
			
			for(int b : bills) {
				System.out.format("%d, ", b);
			}
			
			// get user amount input
			int billMin = 50;
			int userAmountInput;	// Orders amount 
			do {
				System.out.format("%nEnter amount: ");
				userAmountInput = getInputUint();
				if((userAmountInput % billMin) != 0) {		// TODO: magiskt nummer
					System.out.println(userAmountInput + " is not available amount! Amount needs to match available bills!");
				}	
			}while(!(userAmountInput >= billMin));

		if(orderTmode == null) {
			return Optional.empty();
		}
		Order userOrder = new Order(Configuration.getSiteName(), orderCcode, userAmountInput, orderTmode);
		logger.fine(userOrder + " has been placed");
		
		// present order 
		TransactionMode userTmode = (orderTmode.equals(TransactionMode.BUY)) ? TransactionMode.SELL : TransactionMode.BUY;
		System.out.println();
		System.out.println("Your order has been placed: ");
		System.out.format("Site = %s", userOrder.getSite());
		System.out.format("%nTransaction = %s", userTmode.toString());
		System.out.format("%nCurrency = %s", userOrder.getCurrencyCode());
		System.out.format("%nAmount = %s", userOrder.getAmount());
		System.out.format("%n%n");

		return Optional.of(userOrder);	
	}

	/** 
	 * This is a helper method for testing and is used to generate a random Order
	 * @return Optional Order generated randomly
	 */
	public Optional<Order> createOrderRequest() { //first draft
		final String Site = Configuration.getSiteName();
		Random rd = new Random();
		int [] bills = {50, 100, 200, 500, 1000};

		// Selects a random currency ticker from the Currencies Map
		List<String> currencyKeys = new ArrayList<>(Configuration.getCurrencies().keySet());
		String currencyTicker = currencyKeys.get(rd.nextInt(currencyKeys.size())); 

		//Randomizes a amount within the range of the bills array
		int amount = bills[rd.nextInt(bills.length)];

		//Randomizes a TransactionMode either buy or sell.
		int mode = new Random().nextInt(TransactionMode.values().length);
		TransactionMode  transMode = TransactionMode.values()[mode];

		//Order creation based on randomized data.
		Order test = new Order(Site,currencyTicker,amount,transMode);
		Optional<Order> theOrder= Optional.of(test);

		return theOrder;
	}


	/**
	 * Getter for attribute name
	 * @return name a String holding the name of the user
	 */
	public String getName() {
		return name;
	}
}
