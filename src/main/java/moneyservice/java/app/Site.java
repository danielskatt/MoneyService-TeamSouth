package project.java.moneyservice;

import java.util.HashMap;
import java.util.Map;

public class Site implements MoneyService {
	private String Name;
	private String Location;
	Currency currencyRates;

	Map<String, Integer> cash;

	public Site(String Name, Map<String, Integer> cash) {
		this.Name = Name;
		this.Location = Location;

		this.cash = cash;
	}

	public Map<String, Float> readAmountOfCash(){
		return cash;
	}

	public boolean handleOrder(Order orderData) {

		boolean successful = false;

		Currency currencyRates = orderData.currency;

		if(orderData.TransactionMode.BUY) {

			successful = buyMoney(orderData);

		}
		if(orderData.TransactionMode.SELL) {

			successful = sellMoney(orderData);

		}

		return successful;
	}

	public void createReport() {

	}

	public void storeTransaction() {

	}

	@Override
	public boolean buyMoney(Order orderData) throws IllegalArgumentException {

		Currency targetCurrency = findTargetCurrency(orderData.currency.getCurrencyRates());

		int cashOnHand = 0;
		int currencyRate = 0;
		int cashAmountChanged = 0;
		int localCurrency = cash.get(Configuration.LOCAL_CURRENCY);		
	

		currencyRate = targetCurrency.getCurrencyRates();
		cashOnHand = cash.get(targetCurrency.getCurrencyCode());
		
		cashOnHand -= orderData.amount;
		localCurrency += orderData.amount * Configuration.BUY_RATE * currencyRate;
		cash.remove(Configuration.LOCAL_CURRENCY);
		cash.remove(targetCurrency.getCurrencyCode());

		cash.putIfAbsent(Configuration.LOCAL_CURRENCY, localCurrency);
		cash.putIfAbsent(Configuration.LOCAL_CURRENCY, localCurrency);

		return false;
	}

	private Currency findTargetCurrency(String targetCode) {

		Currency targetCurrency;

		// Supposed to loop trough all Enums to find correct currency Enum
		// Then save the rate down and get the ammount of cash on hand for selected currency
		for(Currency tempCurrency : currencyRates.Currency) {
			if(tempCurrency.getCurrencyCode() == targetCode) {
				
				targetCurrency = tempCurrency;
			}
		}

		return targetCurrency;
	}

	
	@Override
	public boolean sellMoney(Order orderData) throws IllegalArgumentException {
		
		Currency targetCurrency = findTargetCurrency(orderData.currency.getCurrencyRates());


		int cashOnHand = 0;
		int currencyRate = 0;
		int cashAmountChanged = 0;
		int localCurrency = cash.get(Configuration.LOCAL_CURRENCY);		


		currencyRate = targetCurrency.getCurrencyRates();
		cashOnHand = cash.get(targetCurrency.getCurrencyCode());

		cashOnHand += orderData.amount;
		localCurrency -= orderData.amount * Configuration.SELL_RATE;

		cash.remove(Configuration.LOCAL_CURRENCY);
		cash.remove(targetCurrency.getCurrencyCode());


		cash.putIfAbsent(Configuration.LOCAL_CURRENCY, localCurrency);
		cash.putIfAbsent(Configuration.LOCAL_CURRENCY, localCurrency);


		return false;
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
