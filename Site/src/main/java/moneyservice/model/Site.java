package moneyservice.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import moneyservice.common.MoneyServiceIO;
import moneyservice.common.Transaction;


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

	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// Variable to hold the amount available of chosen currency
		double cashOnHand;

		// Variable holding the rate for this transaction including 
		double currentRate;

		// Variable holding amount available to use of local currency
		double localCurrency;


		// Holds the currency specified in the orderData
		Currency targetCurrency;
		
		// To make sure the order was ment for just this site
		if(orderData.getSite().equals(name)) {
		try {
			// To get the currency that user wants to buy
			targetCurrency = currencies.get(orderData.getCurrencyCode());

			// Variable to hold the amount available to use of selected currency
			cashOnHand = cash.get(targetCurrency.getCurrencyCode());

			// Variable to hold the currencyRate of chosen rate including the buy rate of the company
			currentRate = Configuration.SELL_RATE * targetCurrency.getRate();

			// Amount on hand of the local currency
			localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

			//	Control to check if transaction are successful
			//	Calculations are made from business perspective
			if((cashOnHand -= orderData.getAmount())>=0) {

				// Calculates the amount of local currency we get from the purchase
				localCurrency += orderData.getAmount() * Configuration.BUY_RATE * currentRate;	


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
			throw new IllegalArgumentException(e.getMessage());
		}
		catch(ClassCastException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
		}
		
		return succesful;
	}


	public boolean sellMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// Variable to hold the amount available of chosen currency
		double cashOnHand;

		// Variable holding the rate for this transaction including 
		double currentRate;

		// Variable holding amount available to use of local currency
		double localCurrency;

		// Holds the currency specified in the orderData
		Currency targetCurrency;

		// To make sure the order was ment for this site
		if(orderData.getSite().equals(name)) {
			try {
				// To get the currency that user wants to buy
				targetCurrency = currencies.get(orderData.getCurrencyCode());

				// Variable to hold the amount available to use of selected currency
				cashOnHand = cash.get(targetCurrency.getCurrencyCode());

				// Variable to hold the currencyRate of chosen rate including the buy rate of the company
				currentRate = Configuration.SELL_RATE * targetCurrency.getRate();

				// Amount on hand of the local currency
				localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

				//	Control to check if transaction are successful
				//	Calculations are made from users perspective
				if((cashOnHand -= orderData.getAmount())>=0) {

					// Calculates the amount of local currency we get from the purchase
					localCurrency += orderData.getAmount() * Configuration.SELL_RATE * currentRate;	

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
				throw new IllegalArgumentException(e.getMessage());
			}
			catch(ClassCastException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}

		return succesful;
	}


	public void printSiteReport(String destination) {
		MoneyServiceIO.storeTransactionsAsSer(destination,transactions);

	}


	public void shutDownService(String destination) {
		// we call printSiteReport to make sure the transactions are stored
		printSiteReport(destination);
	}

	public Map<String, Currency> getCurrencyMap(){
		return currencies;
	}


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
			transactions.add(transaction);
		}
		catch(IllegalArgumentException e) {
			// TODO - Log error message
		}
		catch(NullPointerException e) {
			// TODO - Log error message
		}
	}

}
