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
	 * method made for 
	 * @return Optional - Of type Order
	 */
	public Optional<Order> userCreatedOrder(){
		
		Scanner userInput = new Scanner(System.in);
		Order userOrder = null;
		TransactionMode tmode = null;
		int amount = 0;
		String code = null;
		String mode = null; 
		String site = null;
		boolean accepted = false;
		int i = 0;
		System.out.format("\n Enter following data: \n");
		while(!accepted) {
			try {
				i=0;
				System.out.format("\nEnter requested site: ");
				String input = userInput.next();
				if(input.equalsIgnoreCase("South")||input.equalsIgnoreCase("North")||input.equalsIgnoreCase("West")||input.equalsIgnoreCase("East"))
				{	site =  input; i++; }
				
				System.out.format("\nEnter mode (Buy/Sell): ");
				input = userInput.next();
				if(input.equalsIgnoreCase("Buy")||input.equalsIgnoreCase("Sell"))
				{	mode = input; i++; }
					
				System.out.format("\nEnter currency code (USD,AUD etc): ");
				input = userInput.next();
				if(input.matches("^[A-Z]*$"))
				{	code = input; i++; }
				
				System.out.format("\nEnter amount: ");
				amount = userInput.nextInt();
				
				if(!(mode==null)) {
				if(mode.equalsIgnoreCase("Buy"))
				tmode = TransactionMode.BUY;
			
				if(mode.equalsIgnoreCase("Sell"))
				tmode = TransactionMode.SELL;
				}
			if(i==3) {
				accepted = true;
			}
			else {
				System.out.format("\nWrong inputs in either: Currency code, Buy or Sell, Site");
			}
				
			}catch(InputMismatchException e) {
				site = null;
				mode = null;
				code = null;
				System.out.format("\n Wront input!");
				userInput.nextLine();
			}
		}
		
		userOrder = new Order(site,code,amount,tmode);
		
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
