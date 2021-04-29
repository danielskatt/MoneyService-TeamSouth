package affix.java.project.moneyservice;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;

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
	 * userCreatedOrder - Handles user input for a single order creation
	 * @return Optional - Of type Order
	 */
	public Optional<Order> userCreatedOrder(){
		
		int [] bills = {50, 100, 200, 500, 1000};
		Scanner userInput = new Scanner(System.in);
		TransactionMode tmode = null;
		int amount = 0;
		String code = null;
		String mode = null; 
		String site = null;
		boolean accepted = false,bsite = false, bmode = false, bcode = false, bamount = false; // booleans to enable check for correct inputs
		
		System.out.format("\n Enter following data: \n");
		while(!accepted) {
			try {
				System.out.format("Supported sites: \nEnter requested site: ");
				String input = userInput.next();
				
				// Check for supported site
				if(input.equalsIgnoreCase("South")||input.equalsIgnoreCase("North")||input.equalsIgnoreCase("West")||input.equalsIgnoreCase("East"))
				{	site =  input; bsite = true; }
				
				System.out.format("\nEnter mode (Buy/Sell): ");
				input = userInput.next();
				if(input.equalsIgnoreCase("Buy")||input.equalsIgnoreCase("Sell")) // chek if user entered either buy or sell
				{	mode = input; bmode = true; }
					
				System.out.format("\nEnter currency code (USD,AUD etc): ");
				input = userInput.next();
				if(input.matches("^[A-Z]*$")||input.matches("^[a-z]*$")) // check that the input are only a-z chars
				{	code = input.toUpperCase(); bcode = true; }
				
				
				System.out.format("\nSupported amounts are: 50,100,200,500,100 \nEnter amount: ");
				amount = userInput.nextInt();	
				for(int l : bills) // Control if entered supported amount
				{	if(amount==l) bamount = true; }
				
				
			if(bsite&&bmode&&bcode&&bamount) { // Check that all inputs have been accepted
				accepted = true; 
			} else { bsite = false; bmode = false; bcode = false; bamount = false; }
		
			
			}catch(InputMismatchException e) { // If we get anything except an int value when asking for amount
				System.out.format("\n Wront input! Expected: 0-9"); // Give feedback on wrong input
				userInput.nextLine(); // .nextInt() dosn't read end of string char so its left in buffer. This will remove it
			}
		}	
		if(mode.equalsIgnoreCase("Buy"))
			tmode = TransactionMode.BUY;
			
		if(mode.equalsIgnoreCase("Sell"))
			tmode = TransactionMode.SELL;
		Order userOrder = new Order(site,code,amount,tmode);
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
		List<String> currencyKeys = new ArrayList<>(Configuration.currencies.keySet());
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
