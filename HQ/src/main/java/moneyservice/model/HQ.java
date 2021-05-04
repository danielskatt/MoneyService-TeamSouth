package moneyservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;

public class HQ {
	/**
	 * @attribute name - A String holding the name of HQ
	 */
	private final String name;
	/**
	 * @attribute siteTransactions - A Map holding all the transactions for each Site
	 */
	private final Map<String, List<Transaction>> siteTransactions;

	/**
	 * Constructor for HQ
	 * @param name - A String defining the name of HQ
	 * @param allTransactions - A Map defining all the transactions for each Site
	 */
	public HQ(String name, Map<String, List<Transaction>> allTransactions) {
		this.name = name;
		this.siteTransactions = allTransactions;
	}
	
	/**
	 * A method to get all the currency codes in the List of Transactions between two dates
	 * @param key - A String holding information about which Site to get the currency codes for
	 * @param startDate - A start date with format YYYY-MM-DD
	 * @param endDate - An end date with format YYYY-MM-DD
	 * @return a List of String(s) with all available currency codes
	 */
	public List<String> getCurrencyCodes(String key, LocalDate startDate, LocalDate endDate){
		List<String> availableCodes = new ArrayList<String>();
		if(siteTransactions.containsKey(key) || key.equalsIgnoreCase("ALL")) {
			List<Transaction> transactions = new ArrayList<Transaction>();
			if(key.equalsIgnoreCase("ALL")) {
				transactions = siteTransactions.values()		// get all Transactions
						.stream()								// start a stream
						.flatMap(values -> values.stream())		// turn the map into a stream of Transactions
						.collect(Collectors.toList());			// collect them into one List
			}
			else {
				transactions = siteTransactions.get(key);
			}
			// get all the available currency codes with no doubles
			availableCodes = transactions
					.stream()															// start a stream
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||	// check if time stamp is equal to start date
							(t.getTimeStamp().toLocalDate().isAfter(startDate) &&		// or if it is after start date
							t.getTimeStamp().toLocalDate().isBefore(endDate)) ||		// and it is before end date
							t.getTimeStamp().toLocalDate().isEqual(endDate))			// or if it is equal to end date
					.map(t -> t.getCurrencyCode())										// convert the stream to only handle currency codes
					.distinct()															// sort the currency code in alphabetic order
					.collect(Collectors.toList());										// collect all available element to a List
		}
		return availableCodes;
	}
	
	/**
	 * A method to print all the transactions for the chosen Site
	 * @param key - A String holding information about which Site to get the currency codes for
	 * @param startDate - A start date with format YYYY-MM-DD
	 * @param endDate - An end date with format YYYY-MM-DD
	 */
	public void printTransactions(String key, Period period, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(key) || key.equalsIgnoreCase("ALL")) {
			List<Transaction> transactions = new ArrayList<Transaction>();
			if(key.equalsIgnoreCase("ALL")) {
				transactions = siteTransactions.values()		// get all Transactions
						.stream()								// start a stream
						.flatMap(values -> values.stream())		// turn the map into a stream of Transactions
						.collect(Collectors.toList());			// collect them into one List
			}
			else {
				transactions = siteTransactions.get(key);
			}
			List<Transaction> allTransactions = transactions
					.stream()															// start a stream of the values
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||	// check if time stamp is equal to start date
							(t.getTimeStamp().toLocalDate().isAfter(startDate) &&		// or if it is after start date
							t.getTimeStamp().toLocalDate().isBefore(endDate)) ||		// and it is before end date
							t.getTimeStamp().toLocalDate().isEqual(endDate))			// or if it is equal to end date
					.distinct()															// make sure only has one of each element
					.collect(Collectors.toList());										// collect all the elements to a List
			
			allTransactions.forEach(System.out::println);
		}
	}
	
	/**
	 * A method to print the statistics with information SELL, BUY and Profit
	 * @param key - A String holding information about which Site to get the currency codes for
	 * @param currencyCode - A String with a specific currency code or ALL for all currency codes
	 * @param startDate - A start date with format YYYY-MM-DD
	 * @param endDate - An end date with format YYYY-MM-DD
	 */
	public void printStatistics(String key, Period period, String currencyCode, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(key) || key.equalsIgnoreCase("ALL")) {
			List<Transaction> transactions = new ArrayList<Transaction>();
			IntSummaryStatistics sell;
			IntSummaryStatistics buy;
			if(key.equalsIgnoreCase("ALL")) {
				transactions = siteTransactions.values()		// get all Transactions
						.stream()								// start a stream
						.flatMap(values -> values.stream())		// turn the map into a stream of Transactions
						.collect(Collectors.toList());			// collect them into one List
			}
			else {
				transactions = siteTransactions.get(key);
			}
			if(currencyCode.equalsIgnoreCase("ALL")) {
				sell = transactions
						.stream()
						.filter(cc -> cc.getMode().equals(TransactionMode.BUY))				// filter out only BUY orders (from user perspective)
						.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||	// check if time stamp is equal to start date
								(t.getTimeStamp().toLocalDate().isAfter(startDate) &&		// or if it is after start date
								t.getTimeStamp().toLocalDate().isBefore(endDate)) ||		// and it is before end date
								t.getTimeStamp().toLocalDate().isEqual(endDate))			// or if it is equal to end date
						.collect(Collectors.summarizingInt(Transaction::getAmount));
				
				buy = transactions
						.stream()
						.filter(cc -> cc.getMode().equals(TransactionMode.SELL))				// filter out only SELL orders (from user perspective)
						.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||		// check if time stamp is equal to start date
								(t.getTimeStamp().toLocalDate().isAfter(startDate) &&			// or if it is after start date
								t.getTimeStamp().toLocalDate().isBefore(endDate)) ||			// and it is before end date
								t.getTimeStamp().toLocalDate().isEqual(endDate))				// or if it is equal to end date
						.collect(Collectors.summarizingInt(Transaction::getAmount));
			}
			else {
				sell = transactions
						.stream()
						.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
						.filter(cc -> cc.getMode().equals(TransactionMode.BUY))					// filter out only BUY orders (from user perspective)
						.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||		// check if time stamp is equal to start date
								(t.getTimeStamp().toLocalDate().isAfter(startDate) &&			// or if it is after start date
								t.getTimeStamp().toLocalDate().isBefore(endDate)) ||			// and it is before end date
								t.getTimeStamp().toLocalDate().isEqual(endDate))				// or if it is equal to end date
						.collect(Collectors.summarizingInt(Transaction::getAmount));
				
				buy = transactions
						.stream()
						.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
						.filter(cc -> cc.getMode().equals(TransactionMode.SELL))				// filter out only BUY orders (from user perspective)
						.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate) ||		// check if time stamp is equal to start date
								(t.getTimeStamp().toLocalDate().isAfter(startDate) &&			// or if it is after start date
								t.getTimeStamp().toLocalDate().isBefore(endDate)) ||			// and it is before end date
								t.getTimeStamp().toLocalDate().isEqual(endDate))				// or if it is equal to end date
						.collect(Collectors.summarizingInt(Transaction::getAmount));			
			}
			System.out.format("Statistics for site %s %s %s - Currency: %s%n", key.toUpperCase(), period.getName().toUpperCase(), startDate, currencyCode);
			System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell.getSum() + " SEK");
			System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy.getSum() + " SEK");
			System.out.println("Profit " + (sell.getSum() - buy.getSum()) + " SEK");
		}
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the siteTransactions
	 */
	public Map<String, List<Transaction>> getSiteTransactions() {
		return siteTransactions;
	}
	
	
}
