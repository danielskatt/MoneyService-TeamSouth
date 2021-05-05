package affix.java.project.moneyservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import moneyservice.site.library.MoneyService;


public class Site implements MoneyService {

	/**
	 * @attribute logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}

	/**
	 * @attribute name a String holding name of the Money Service site
	 */
	private String name;

	/**
	 * @attribute cash a Map<String, Double> with a String holding the code of the 
	 * currency (three capital letters) and amount of each currency
	 */
	private Map<String, Double> cash;

	/**
	 * @attribute currencies a Map<String, Currency> with a String holding the code 
	 * of the currency (three capital letters) and corresponding Currency object
	 */
	private Map<String, Currency> currencies; 

	/**
	 * @attribute transactions a List<Transaction> holding each transaction made for the day
	 */
	private List<Transaction> transactions;


	//	/**
	//	 * Default constructor for creating a complete Site object by using name.
	//	 * @param name a String holding name of the Money Service site
	//	 * @throws IllegalArgumentException String name is empty 
	//	 */
	//	public Site(String name) {
	//		if(name.isEmpty()) {
	//			throw new IllegalArgumentException("Site name can NOT be empty!");
	//		}
	//		this.name = name;
	//		this.cash = Configuration.getBoxOfCash();
	//		this.currencies = Configuration.getCurrencies();
	//		this.transactions = new ArrayList<Transaction>();
	//	}

	/**
	 * Default constructor for creating a complete Site object.
	 * @param name a String holding name of the Money Service site
	 * @param cash a Map<String, Double> with a String holding the code of the 
	 * currency (three capital letters) and amount of each currency
	 * @param currencies a Map<String, Currency> with a String holding the code 
	 * of the currency (three capital letters) and corresponding Currency object
	 * @throws IllegalArgumentException if parameters does not match requirements
	 */
	public Site(String name, Map<String, Double> cash, Map<String, Currency> currencies) { 

		if(name.isEmpty()) {
			logger.log(Level.SEVERE, "Error: Site name is empty!");
			throw new IllegalArgumentException("Error: Site name is empty!");
		}

		if(cash.isEmpty()) {
			logger.log(Level.SEVERE, "Error: Box of cash is empty!");
			throw new IllegalArgumentException("Error: Box of cash is empty!");
		}

		if(currencies.isEmpty()) {
			logger.log(Level.SEVERE, "Error: Currencies is empty!");
			throw new IllegalArgumentException("Error: Currencies is empty!");
		}

		this.name = name;
		this.cash = cash;
		logger.fine("Site has been provided with boxOfCash");
		this.currencies = currencies;
		logger.fine("Site has been provided with the currencies");
		this.transactions = new ArrayList<Transaction>();
	}

	/**
	 * This method is used to buy money with Local Currency from a User 
	 * @param orderData an Order holding data
	 * @return boolean true if the order was successful
	 * @throws IllegalArgumentException if required currency is not accepted
	 */
	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// To make sure the order was meant for just this site
		if(orderData.getSite().equals(name)) {
			try {

				if(currencies.get(orderData.getCurrencyCode()) == null) {
					throw new IllegalArgumentException("Currency code could not be found!");
				}
				// To get the currency that user wants to buy
				Currency targetCurrency = currencies.get(orderData.getCurrencyCode());
				logger.finer(targetCurrency + " has been read in as the target currency");

				if(cash.get(targetCurrency.getCurrencyCode()) == null){
					// TODO - Change this to false if we implement the update in Configuration class
					cash.putIfAbsent(targetCurrency.getCurrencyCode(), (double)0);
					logger.finer(targetCurrency.getCurrencyCode() + " is not found in BoxOfCash " + 
							" setting the amount to 0");
				}

				// Variable to hold the amount available to use of selected currency
				double cashOnHand = cash.get(targetCurrency.getCurrencyCode());
				logger.finer(cashOnHand + " " + targetCurrency.getCurrencyCode() + " is available ");

				// Variable to hold the currencyRate of chosen rate including the buy rate of the company
				double currentRate = Configuration.BUY_RATE * targetCurrency.getRate();
				logger.finer("The current rate is " + currentRate);

				// Amount on hand of the local currency
				double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);
				logger.finer("Current amount in local currency: " + (int)localCurrency + " "+ Configuration.LOCAL_CURRENCY);

				//	Control to check if transaction are successful
				//	Calculations are made from business perspective
				if((currentRate * orderData.getAmount())<= localCurrency) {

					// Calculates the amount of local currency we get from the purchase
					localCurrency -= orderData.getAmount() * currentRate;	


					// Adds the new amount to the map with correct key
					cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
					logger.finer("Site bought " + orderData.getAmount() + " of " + targetCurrency +
							" Amount of cash left after buy: " + (int)localCurrency + " " + Configuration.LOCAL_CURRENCY);

					// Adds the new amount to the map with correct key
					cash.replace(orderData.getCurrencyCode(), cashOnHand+orderData.getAmount());
					logger.finer("New amount for " + orderData.getCurrencyCode() + " : " + cashOnHand+orderData.getAmount());

					// Stores the order to enable printOut of all transactions made for the day
					storeTransaction(orderData);

					succesful = true;

				}
			}
			// If above try statement fails it is because some error with key during calculations made above
			catch(NullPointerException e) {
				// Throws an IllegalArgumentException to comply with function statement
				logger.log(Level.SEVERE, e.getMessage());
			}
			catch(ClassCastException e) {
				logger.log(Level.SEVERE, e.getMessage());
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
					throw new IllegalArgumentException("Currency code could not be found!");
				}

				Currency targetCurrency = currencies.get(orderData.getCurrencyCode());
				logger.finer(targetCurrency + " has been read in as the target currency");

				if(cash.get(targetCurrency.getCurrencyCode()) == null){
					return false;
				}
				// Variable to hold the amount available to use of selected currency
				double cashOnHand = cash.get(targetCurrency.getCurrencyCode());
				logger.finer(cashOnHand + " " + targetCurrency.getCurrencyCode() + " is available ");

				// Variable to hold the currencyRate of chosen rate including the sell rate of the company
				double currentRate = Configuration.SELL_RATE * targetCurrency.getRate();
				logger.finer("The current rate is " + currentRate);

				// Amount on hand of the local currency
				double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);
				logger.finer("Current amount in local currency: " + (int)localCurrency + Configuration.LOCAL_CURRENCY);

				//	Control to check if transaction are successful
				//	Calculations are made from users perspective
				if((cashOnHand -= orderData.getAmount())>=0) {

					// Calculates the amount of local currency we get from the purchase
					localCurrency += orderData.getAmount() * currentRate;	

					// Adds the new amount to the map with correct key
					cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
					logger.finer("Site sold " + orderData.getAmount() + " of " + targetCurrency +
							" Amount of cash after sell: " + (int)localCurrency + " " + Configuration.LOCAL_CURRENCY);

					// Adds the new amount to the map with correct key
					cash.replace(orderData.getCurrencyCode(), cashOnHand);
					logger.finer("New amount for " + orderData.getCurrencyCode() + " : " + cashOnHand+orderData.getAmount());

					// Stores the order to enable printOut of all transactions made for the day
					storeTransaction(orderData);

					succesful = true;

				}
			}
			catch(NullPointerException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
			catch(ClassCastException e) {
				logger.log(Level.SEVERE, e.getMessage());
			}
		}

		return succesful;
	}

	/**
	 * Print the current status of Box of Cash into a textfile
	 */
	public void printSiteReport(String destination) {
		logger.fine("Storing daily report in textfile: " + destination);
		MoneyServiceIO.storeBoxOfCashAsText(destination, cash);

	}

	/**
	 * Close the Site and print all the transactions to a .ser file and 
	 * the current status of the box of cash into a text file
	 */
	public void shutDownService(String destination) {
		// we call printSiteReport to make sure the transactions are stored
		logger.info("Shutting down!");
		logger.fine("Storing daily transactions as serializable in file "+ destination);
		MoneyServiceIO.storeTransactionsAsSer(destination,transactions);
		String filenameReport = "SiteReports/SiteReport_" + name + "_" + Configuration.getCURRENT_DATE().toString() + ".txt";
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
				logger.fine(transaction + " was completed");
			}
		}
		catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		catch(NullPointerException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

}
