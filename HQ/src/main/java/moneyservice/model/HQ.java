package moneyservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import affix.java.project.moneyservice.Configuration;
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
	public void printStatistics(String key, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(key) || key.equalsIgnoreCase("ALL")) {
			Map<String, Integer> siteStatistics = new TreeMap<String, Integer>();
			List<Transaction> transactions = new ArrayList<Transaction>();
			int sell = 0;
			int buy = 0;
			int sumSiteSell = 0;
			int sumSiteBuy = 0;
			if(key.equalsIgnoreCase("ALL")) {
				List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
				for(LocalDate date : dates) {
					String filename = "DailyRates/DETALJERAT RESULTAT_" + date.toString() + ".txt";
					Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
					if(!currencies.isEmpty()) {
						for(MoneyServiceSites site : MoneyServiceSites.values()) {
							if(!site.equals(MoneyServiceSites.NONE) && !site.equals(MoneyServiceSites.ALL)) {
								siteStatistics = printStatisticsEachSite(site.getName(), period, currencyCode, availableCurrencies, date, currencies);
								sumSiteSell += siteStatistics.get(TransactionMode.SELL.name());
								sumSiteBuy += siteStatistics.get(TransactionMode.BUY.name());
							}
						}
						if(!period.equals(Period.DAY)) {
							printReportPeriod(key, period, sumSiteSell, sumSiteBuy, currencyCode);					
						}
					}
				}
			}
			else {
				List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
				int sumPeriodSell = 0;
				int sumPeriodBuy = 0;
				for(LocalDate date : dates) {
					String filename = "DailyRates/DETALJERAT RESULTAT_" + date.toString() + ".txt";
					Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
					if(!currencies.isEmpty()) {
						transactions = siteTransactions.get(key);
						if(currencyCode.equalsIgnoreCase("ALL")) {
							int sumSell = 0;
							int sumBuy = 0;
							for(String currency : availableCurrencies) {
								sell = getStatisticsEachDay(transactions, currency, TransactionMode.SELL, date, currencies);
								buy = getStatisticsEachDay(transactions, currency, TransactionMode.BUY, date, currencies);
								if(dates.size() == 1) {
									printReportEachDay(key, period, sell, buy, date, currency);									
								}
								sumSell += sell;
								sumBuy += buy;
							}
							printReportEachDay(key, period, sumSell, sumBuy, date, currencyCode);
						}
						else {
							sell = getStatisticsEachDay(transactions, currencyCode, TransactionMode.SELL, date, currencies);
							buy = getStatisticsEachDay(transactions, currencyCode, TransactionMode.BUY, date, currencies);
							printReportEachDay(key, period, sell, buy, date, currencyCode);
						}
						sumPeriodSell += sell;
						sumPeriodBuy += buy;
					}
				}
				if(dates.size() > 1) {
					printReportPeriod(key, period, sumPeriodSell, sumPeriodBuy, currencyCode);					
				}
			}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param period
	 * @param currencyCode
	 * @param availableCurrencies
	 * @param date
	 * @param currencies
	 * @return
	 */
	private Map<String, Integer> printStatisticsEachSite(String key, Period period, String currencyCode, List<String> availableCurrencies, LocalDate date, Map<String, Currency> currencies) {
		Map<String, Integer> sellAndBuy = new TreeMap<String, Integer>();
		int sell = 0, buy = 0;
		int sumSiteDaySell = 0, sumSiteDayBuy = 0;
		int sumCurrencySell = 0, sumCurrencyBuy = 0;
		if(!currencies.isEmpty()) {
			if(siteTransactions.containsKey(key)) {
				List<Transaction> transactions = siteTransactions.get(key);
				if(currencyCode.equalsIgnoreCase("ALL")) {
					for(String currency : availableCurrencies) {
						sell = getStatisticsEachDay(transactions, currency, TransactionMode.SELL, date, currencies);
						buy = getStatisticsEachDay(transactions, currency, TransactionMode.BUY, date, currencies);
						sumCurrencySell += sell;
						sumCurrencyBuy += buy;
					}
					if(period.equals(Period.DAY) || period.equals(Period.WEEK)) {
						printReportEachDay(key, period, sumCurrencySell, sumCurrencyBuy, date, currencyCode);										
					}
				}
				else {
					sell = getStatisticsEachDay(transactions, currencyCode, TransactionMode.SELL, date, currencies);
					buy = getStatisticsEachDay(transactions, currencyCode, TransactionMode.BUY, date, currencies);
					if(period.equals(Period.DAY) || period.equals(Period.WEEK)) {
						printReportEachDay(key, period, sell, buy, date, currencyCode);										
					}
				}
				sumSiteDaySell += sell;
				sumSiteDayBuy += buy;
			}
		}
		if(period.equals(Period.DAY) || period.equals(Period.WEEK)) {
			printReportEachDay(key, period, sumSiteDaySell, sumSiteDayBuy, date, currencyCode);
			System.out.format("-------------------------------------- %n%n");							
		}
		sellAndBuy.putIfAbsent(TransactionMode.SELL.name(), sumSiteDaySell);
		sellAndBuy.putIfAbsent(TransactionMode.BUY.name(), sumSiteDayBuy);
		
		return sellAndBuy;

	}
	
	/**
	 * 
	 * @param transactions
	 * @param currencyCode
	 * @param mode
	 * @param startDate
	 * @param currencies
	 * @return
	 */
	private int getStatisticsEachDay(List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate startDate, Map<String, Currency> currencies) {
		int statistics = 0;
		if(transactions != null){
			if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
				if(currencyCode.equals("ALL")) {
					float currencyRate = currencies.get(currencyCode).getRate();
					statistics = (int)transactions
							.stream()
							.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
							.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate))				// or if it is equal to end date
							.mapToDouble(t -> t.getAmount() * currencyRate)
							.sum();			
				}
				else {
					float currencyRate = currencies.get(currencyCode).getRate();
					statistics = (int)transactions
							.stream()
							.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
							.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
							.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate))				// or if it is equal to end date
							.mapToDouble(t -> t.getAmount() * currencyRate)
							.sum();
				}
			}
		}
		return statistics;
	}
	
	/**
	 * 
	 * @param key
	 * @param period
	 * @param sell
	 * @param buy
	 * @param startDate
	 * @param currencyCode
	 */
	private static void printReportEachDay(String key, Period period, int sell, int buy, LocalDate startDate, String currencyCode) {
		System.out.format("Statistics for site %s %s %s - Currency: %s%n", key.toUpperCase(), period.getName().toUpperCase(), startDate, currencyCode);
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy + " SEK");
		System.out.println("Profit " + (sell - buy) + " SEK");
		System.out.println();
	}
	
	/**
	 * 
	 * @param key
	 * @param period
	 * @param sell
	 * @param buy
	 * @param currencyCode
	 */
	private static void printReportPeriod(String key, Period period, int sell, int buy, String currencyCode) {
		System.out.format("Statistics for site %s %s ALL - Currency: %s%n", key.toUpperCase(), period.getName().toUpperCase(), currencyCode);
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy + " SEK");
		System.out.println("Profit " + (sell - buy) + " SEK");
		System.out.println();
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
