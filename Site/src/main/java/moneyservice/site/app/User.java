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
	 * @attribute logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}
	
	/**
	 * userCreatedOrder - Handles user input for a single order creation
	 * @return Optional - Of type Order
	 */
	public Optional<Order> userCreatedOrder(){
		
		int [] bills = {50, 100, 200, 500, 1000};
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
				if(modeInput.equalsIgnoreCase("Buy")||modeInput.equalsIgnoreCase("Sell")) // chek if user entered either buy or sell
				{	mode = modeInput; bmode = true; }
		
				System.out.format("\nEnter currency code (USD,AUD etc): ");
				codeInput = userInput.next();
				if(codeInput.matches("^[A-Z]*$")||codeInput.matches("^[a-z]*$")) // check that the input are only a-z chars
				{	code = codeInput.toUpperCase(); bcode = true; }
				
				System.out.format("\nSupported amounts are: 50,100,200,500,1000 \nEnter amount: ");
				amount = userInput.nextInt();	
				for(int l : bills) // Control if entered supported amount
				{	if(amount==l) bamount = true; }
				
				
			if(bmode&&bcode&&bamount) { // Check that all inputs have been accepted
				accepted = true; 
			} else { bmode = false; bcode = false; bamount = false; 
				logger.log(Level.WARNING,"\nEntered wrong input, try again!" );
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
		
		Order userOrder = new Order("South",code,amount,tmode); // Configuration.getSiteName() should replace "South"
		userInput.close();
		
		return Optional.of(userOrder);	
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
	 * Method that will return the name of the user.
	 * @return name - name of the user.
	 */
	public String getName() {
		return name;
	}
	
}
