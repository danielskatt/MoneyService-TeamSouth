package moneyservice.java.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Site implements MoneyService {
	private String Name;
	private String Location;
	private Currency currencyRates;

	private Map<String, Integer> cash;
	private Map<String, Currency> currencies; 
	
	private List<Transaction> transactions = new ArrayList<Transaction>();

	
	public Site(String Name) {
		this.Name = Name;
		this.cash = Configuration.getBoxOfCash();
		this.currencies = Configuration.getCurrencies();
	}

	public Map<String, Integer> readAmountOfCash(){
		return cash;
	}

	public void createReport() {

	}

	private void storeTransaction(Order orderData) {
		
		Transaction transaction = new Transaction(orderData.amount, orderData.getCurrencyCode);
		
		transactions.add(transaction);
	}

	
	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		// boolean to hold if transaction was succesul or not
		boolean succesful = false;
		
		// To get the currency that user wants to buy
		Currency targetCurrency = currencies.get(orderData.getCurrencyCode());

		// Variable to hold the amount available to use of selected currency
		int cashOnHand =  cash.get(targetCurrency.getCurrencyCode());
		
		// Variable to hold the currencyRate of chosen rate including the buy rate of the company
		int currentRate = Configuration.SELL_RATE * targetCurrency.getCurrencyRate();
		
		// Amount on hand of the local currency
		int localCurrency = cash.get(Configuration.LOCAL_CURRENCY);
			
//		Control to check if transaction are successful
//		Calculations are made from users perspective
		if((cashOnHand -= orderData.amount)>0) {
			
			// Calculates the amount of local currency we get from the purchase
			localCurrency += orderData.amount * Configuration.BUY_RATE * currentRate;	

	
			// Adds the new amount to the map with correct key
			cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
			cash.replace(orderData.getCurrencyCode(), cashOnHand);

			storeTransaction(orderData);
			
			succesful = true;
		}
		
		
		return succesful;
	}


	public boolean sellMoney(Order orderData) throws IllegalArgumentException {
		
		// boolean to hold if transaction was succesul or not
		boolean succesful = false;
		
		// To get the currency that user wants to buy
		Currency targetCurrency = currencies.get(orderData.getCurrencyCode());

		// Variable to hold the amount available to use of selected currency
		int cashOnHand =  cash.get(targetCurrency.getCurrencyCode());
		
		// Variable to hold the currencyRate of chosen rate including the buy rate of the company
		int currentRate = Configuration.SELL_RATE * targetCurrency.getCurrencyRate();
		
		// Amount on hand of the local currency
		int localCurrency = cash.get(Configuration.LOCAL_CURRENCY);
			
//		Control to check if transaction are successful
//		Calculations are made from users perspective
		if((cashOnHand -= orderData.amount)>0) {
			
			// Calculates the amount of local currency we get from the purchase
			localCurrency += orderData.amount * Configuration.BUY_RATE * currentRate;	

	
			// Adds the new amount to the map with correct key
			cash.replace(Configuration.LOCAL_CURRENCY, localCurrency);
			cash.replace(orderData.getCurrencyCode(), cashOnHand);

			storeTransaction(orderData);
			
			succesful = true;
		}
		
		
		return succesful;
	}

	
	@Override
	public void printSiteReport(String destination) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutDownService(String destination) {
		// TODO Auto-generated method stub

	}
}
