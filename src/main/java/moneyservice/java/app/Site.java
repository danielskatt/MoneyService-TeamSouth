package moneyservice.java.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


// TODO: Function comment summaries
public class Site implements MoneyService {

	// Variable holding the name of the site
	private String name;

	// Map holding the amount of each currency available to use. Uses currencyCode as key
	private Map<String, Double> cash;

	// Map holding the currency rate of each currency. Uses currencyCode as key
	private Map<String, Currency> currencies; 

	// List to store the transactions made for the day. 
	private List<Transaction> transactions;

	// ========== Contructor ==========
	public Site(String Name) {
		this.name = Name;
		this.cash = Configuration.getBoxOfCash();
		this.currencies = Configuration.getCurrencies();
		this.transactions = new ArrayList<Transaction>();
	}

	// TODO: Add try and Catch statements
	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// To get the currency that user wants to buy
		Currency targetCurrency = currencies.get(orderData.getCurrencyCode());

		// Variable to hold the amount available to use of selected currency
		double cashOnHand =  cash.get(targetCurrency.getCurrencyCode());

		// Variable to hold the currencyRate of chosen rate including the buy rate of the company
		double currentRate = Configuration.SELL_RATE * targetCurrency.getCurrencyRate();

		// Amount on hand of the local currency
		double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

		//		Control to check if transaction are successful
		//		Calculations are made from users perspective
		if((cashOnHand -= orderData.getAmount())>0) {

			// Calculates the amount of local currency we get from the purchase
			localCurrency += orderData.getAmount() * Configuration.BUY_RATE * currentRate;	

			// Adds the new amount to the map with correct key
			cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
			cash.replace(orderData.getCurrencyCode(), cashOnHand);

			// Stores the order to enable printOut of all transactions made for the day
			storeTransaction(orderData);

			succesful = true;
		}

		return succesful;
	}

	// TODO: Add try and Catch statements
	public boolean sellMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was successful or not
		boolean succesful = false;

		// To get the currency that user wants to buy
		Currency targetCurrency = currencies.get(orderData.getCurrencyCode());

		// Variable to hold the amount available to use of selected currency
		double cashOnHand = cash.get(targetCurrency.getCurrencyCode());

		// Variable to hold the currencyRate of chosen rate including the buy rate of the company
		double currentRate = Configuration.SELL_RATE * targetCurrency.getCurrencyRate();

		// Amount on hand of the local currency
		double localCurrency = cash.get(Configuration.LOCAL_CURRENCY);

		//	Control to check if transaction are successful
		//	Calculations are made from users perspective
		if((cashOnHand -= orderData.getAmount())>0) {

			// Calculates the amount of local currency we get from the purchase
			localCurrency += orderData.getAmount() * Configuration.BUY_RATE * currentRate;	


			// Adds the new amount to the map with correct key
			cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
			cash.replace(orderData.getCurrencyCode(), cashOnHand);

			// Stores the order to enable printOut of all transactions made for the day
			storeTransaction(orderData);

			succesful = true;
		}
		return succesful;
	}

	// TODO: implement function
	// TODO: Add try and Catch statements
	public void printSiteReport(String destination) {
		// TODO Auto-generated method stub

	}

	// TODO: implement function
	// TODO: Add try and Catch statements
	public void shutDownService(String destination) {
		// TODO Auto-generated method stub

	}

	// Returns the Map holding the currencies and currency rates
	public Map<String, Currency> getCurrencyMap(){
		return currencies;
	}

	//	A function that returns the amount available of specified currency	 
	public Optional<Double> getAvailableAmount(String currencyCode) {

		// if any amount are available of specified currency 
		// then it returns that amount
		// if not return an empty Optional
		if(cash.get(currencyCode)>0) {
			int amount = cash.get(currencyCode);
			return Optional.of((double)amount);
		}


		return Optional.empty(); 
	}

	// TODO: Add try and Catch statements
	private void storeTransaction(Order orderData) {
		
		Transaction transaction = new Transaction(orderData);

		// Adds the transaction to the list of transactions for the day
		transactions.add(transaction);
	}

}
