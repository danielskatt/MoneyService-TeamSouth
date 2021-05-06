package moneyservice.site.app;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
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
	 * This method lets a user create an Order through CLI
	 * @return Optional Order created by a User
	 */
	public Optional<Order> userCreatedOrder(){
		
		int [] bills = {50, 100, 200, 500, 1000};
		@SuppressWarnings("resource")
		Scanner userInput = new Scanner(System.in);
		TransactionMode tmode = null;
		int amount = 0;
		String code = null;
		String modeInput = null;
		String codeInput = null;
		String mode = null; 
		boolean accepted = false,bmode = false, bcode = false, bamount = false; // booleans to enable check for correct inputs
		
		while(!accepted) {
			try {
			
				System.out.format("\nEnter mode (Buy/Sell): ");
				modeInput = userInput.next();
				if(modeInput.equalsIgnoreCase("Buy")||modeInput.equalsIgnoreCase("Sell")) // check if user entered either buy or sell
				{	mode = modeInput; bmode = true; }
		
				System.out.format("\nEnter currency code (USD,AUD etc): ");
				codeInput = userInput.next();
				if(codeInput.matches("^[A-Z]*$")||codeInput.matches("^[a-z]*$") && codeInput.length()==3) // check that the input are only a-z chars
				{	code = codeInput.toUpperCase(); bcode = true; }
				
				System.out.format("\nSupported amounts are: 50,100,200,500,1000 \nEnter amount: ");
				amount = userInput.nextInt();	
				for(int l : bills) // Control if entered supported amount
				{	if(amount==l) bamount = true; }
				
				
			if(bmode&&bcode&&bamount) { // Check that all inputs have been accepted
				accepted = true; 
			} else { bmode = false; bcode = false; bamount = false; 
				System.out.println("\nEntered wrong input, try again!");
			}
			
			}catch(InputMismatchException e) { // If we get anything except an int value when asking for amount
				logger.log(Level.WARNING, "Wrong input! Expected: 0-9");
				userInput.nextLine(); // .nextInt() dosn't read end of string char so its left in buffer. This will remove it
			}
		}	
		if(mode.equalsIgnoreCase("Buy"))
			tmode = TransactionMode.BUY;
			
		if(mode.equalsIgnoreCase("Sell"))
			tmode = TransactionMode.SELL;

		Order userOrder = new Order(Configuration.getSiteName(),code,amount,tmode); // Configuration.getSiteName() should replace "South"
		logger.fine(userOrder + " has been placed");
		userInput.nextLine();
		
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
