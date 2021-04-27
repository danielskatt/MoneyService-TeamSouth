package affix.java.project.moneyservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Site implements MoneyService {

	/**
	 * @attribute name - Holds the information about the name of this site
	 */
	private String name;

	/**
	 * @attribute cash - Holds information about the amount of cash for each currency, for example USD 300
	 */
	private Map<String, Double> cash;

	/**
	 * @attribute currencies - Holds information about what type of currencies and their currency rate, for example USD 8.67
	 */
	private Map<String, Currency> currencies; 

	/**
	 * @attribute transactions - Holds information about each transaction made for the day
	 */
	private List<Transaction> transactions;
	
	private static Logger logger;
	
	static{
		logger = Logger.getLogger("affix.java.project.moneyservice");
	}

	/**
	 *  Constructor
	 * @param name - Defines the name for this object
	 */
	public Site(String name) {
		this.name = name;
		this.cash = Configuration.getBoxOfCash();
		this.currencies = Configuration.getCurrencies();
		this.transactions = new ArrayList<Transaction>();
	}
	
	/**
	 * This method is used to buy money from a User and return the corresponding value in Local Currency 
	 * @param orderData - an Order
	 * @return boolean true if the order was successful
	 * @throws IllegalArgumentException if required currency is not accepted
	 */
	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;
		
		// To make sure the order was ment for just this site
		if(orderData.getSite().equals(name)) {
			try {
				
				if(currencies.get(orderData.getCurrencyCode()) == null) {
					throw new IllegalArgumentException("Currency is not supported!");
				}
				// To get the currency that user wants to buy
				Currency targetCurrency = currencies.get(orderData.getCurrencyCode());
				
				if(cash.get(targetCurrency.getCurrencyCode()) == null){
					// TODO - Change this to false if we implement the update in Configuration class
					cash.put(targetCurrency.getCurrencyCode(), (double)orderData.getAmount());
				}

				// Variable to hold the amount available to use of selected currency
				double cashOnHand = cash.get(targetCurrency.getCurrencyCode());

				// Variable to hold the currencyRate of chosen rate including the buy rate of the company
				double currentRate = Configuration.BUY_RATE * targetCurrency.getRate();

				// Amount on hand of the local currency
				double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

				//	Control to check if transaction are successful
				//	Calculations are made from business perspective
				if((currentRate * orderData.getAmount())<= localCurrency) {

					// Calculates the amount of local currency we get from the purchase
					localCurrency -= orderData.getAmount() * currentRate;	


					// Adds the new amount to the map with correct key
					cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);

					// Adds the new amount to the map with correct key
					cash.replace(orderData.getCurrencyCode(), cashOnHand+orderData.getAmount());


					// Stores the order to enable printOut of all transactions made for the day
					storeTransaction(orderData);
					
					succesful = true;

				}
			}
			// If above try statement fails it is because some error with key during calculations made above
			catch(NullPointerException e) {
				// Throws an IllegalArgumentException to comply with function statement
				System.out.println("The currency is not supported");
			}
			catch(ClassCastException e) {
				System.out.println(e.getMessage());
			}
		}
		
		return succesful;
	}

	/**
	 * This method is used to sell money to a User and return the corresponding value in desired currency 
	 * @param orderData - an Order
	 * @return boolean true if the order was successful
	 * @throws IllegalArgumentException if required currency is not accepted
	 */
	public boolean sellMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// To make sure the order was ment for this site
		if(orderData.getSite().equals(name)) {
			try {
				// To get the currency that user wants to buy
				
				if(currencies.get(orderData.getCurrencyCode()) == null) {
					throw new IllegalArgumentException("Currency is not supported!");
				}
		
				Currency targetCurrency = currencies.get(orderData.getCurrencyCode());

				if(cash.get(targetCurrency.getCurrencyCode()) == null){
					return false;
				}
				// Variable to hold the amount available to use of selected currency
				double cashOnHand = cash.get(targetCurrency.getCurrencyCode());

				// Variable to hold the currencyRate of chosen rate including the sell rate of the company
				double currentRate = Configuration.SELL_RATE * targetCurrency.getRate();

				// Amount on hand of the local currency
				double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

				//	Control to check if transaction are successful
				//	Calculations are made from users perspective
				if((cashOnHand -= orderData.getAmount())>=0) {

					// Calculates the amount of local currency we get from the purchase
					localCurrency += orderData.getAmount() * currentRate;	

					// Adds the new amount to the map with correct key
					cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);


					// Adds the new amount to the map with correct key
					cash.replace(orderData.getCurrencyCode(), cashOnHand);

					// Stores the order to enable printOut of all transactions made for the day
					storeTransaction(orderData);

					succesful = true;

				}
			}
			// If above try statement fails it is because some error with key during calculations made above
			catch(NullPointerException e) {
				// Throws an IllegalArgumentException to comply with function statement
				System.out.println("The currency is not supported");
			}
			catch(ClassCastException e) {
				System.out.println(e.getMessage());
			}
		}

		return succesful;
	}

	/**
	 * Print the current status of Box of Cash into a textfile
	 */
	public void printSiteReport(String destination) {
		logger.info("Storing transactions in file!");
		MoneyServiceIO.storeBoxOfCashAsText(destination, cash);

	}

	/**
	 * Close the Site and print all the transactions to a .ser file and 
	 * the current status of the box of cash into a text file
	 */
	public void shutDownService(String destination) {
		// we call printSiteReport to make sure the transactions are stored
		logger.info("Shutting down!");
		MoneyServiceIO.storeTransactionsAsSer(destination,transactions);
		String filenameReport = "../HQ/SiteReports/SiteReport_" + name + Configuration.getCURRENT_DATE().toString() + ".txt";
		printSiteReport(filenameReport);
	}
	
	/**
	 * @return currencies
	 */
	public Map<String, Currency> getCurrencyMap(){
		return currencies;
	}

	/**
	 * @return the amount for the desired currency. Is optional.empty() if the currency is not found
	 */
	public Optional<Double> getAvailableAmount(String currencyCode) {

		// if any amount are available of specified currency 
		// then it returns that amount
		// if not return an empty Optional
		if(cash.get(currencyCode) != null) {
			if(cash.get(currencyCode)>0) {
				double amount = cash.get(currencyCode);
				return Optional.of((double)amount);
			}			
		}

		return Optional.empty(); 
	}

	/**
	 * Method used to create and store transactions in the transaction attribute
	 * @param orderData - holding value, currencyCode and transaction mode 
	 */
	private void storeTransaction(Order orderData) {

		Transaction transaction = new Transaction(orderData);

		try {
			// Adds the transaction to the list of transactions for the day
			boolean stored = transactions.add(transaction);
			if(stored) {
				logger.fine(transaction + " was stored");
			}
		}
		catch(IllegalArgumentException e) {
			// TODO - Log error message
			logger.log(Level.WARNING, "An exception has occured",e);
		}
		catch(NullPointerException e) {
			// TODO - Log error message
			logger.log(Level.WARNING, "An exception has occured",e);
		}
	}

}
