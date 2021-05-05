package moneyservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;

public class HQ {
	/**
	 * @attribute name A String holding the name of HQ
	 */
	private final String name;
	/**
	 * @attribute siteTransactions A Map holding all the transactions for each Site
	 */
	private final Map<String, List<Transaction>> siteTransactions;
	/**
	 * @attribute sites A List holding all the available sites
	 */
	private final List<String> sites;

	/**
	 * Constructor for HQ
	 * @param name - A String defining the name of HQ
	 * @param allTransactions - A Map defining all the transactions for each Site
	 */
	public HQ(String name, Map<String, List<Transaction>> allTransactions, List<String> sites) {
		this.name = name;
		this.siteTransactions = allTransactions;
		this.sites = sites;
	}
	
	/**
	 * A method to get all the currency codes in the List of Transactions between two dates
	 * @param key - A String holding information about which Site to get the currency codes for
	 * @param startDate - A start date with format YYYY-MM-DD
	 * @param endDate - An end date with format YYYY-MM-DD
	 * @return a List of String(s) with all available currency codes
	 */
	public List<String> getAvailableCurrencyCodes(String key, LocalDate startDate, LocalDate endDate){
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
					.filter(filterPeriod(startDate, endDate))
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
					.stream()									// start a stream of the values
					.filter(filterPeriod(startDate, endDate))	// or if it is equal to end date
					.distinct()									// make sure only has one of each element
					.collect(Collectors.toList());				// collect all the elements to a List
			
			allTransactions.forEach(System.out::println);
		}
	}
	
	/**
	 * A Predicate for filtering out Transactions between a specific period
	 * @param startDate - the start date for the period
	 * @param endDate - the end date (included) for the period
	 * @return true if the date is within or equal start date and end date
	 */
	private static Predicate<Transaction> filterPeriod(LocalDate startDate, LocalDate endDate){
		return t -> (t.getTimeStamp().toLocalDate().isEqual(startDate) ||				// check if time stamp is equal to start date
							(t.getTimeStamp().toLocalDate().isAfter(startDate) &&		// or if it is after start date
							t.getTimeStamp().toLocalDate().isBefore(endDate)) ||		// and it is before end date
							t.getTimeStamp().toLocalDate().isEqual(endDate));
	};
	
	/**
	 * 
	 * @param site
	 * @param period
	 * @param currencyCode
	 * @param availableCurrencies
	 * @param startDate
	 * @param endDate
	 */
	public void printStatisticsDay(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
			for(LocalDate date : dates) {
				int sumSiteSell = 0;
				int sumSiteBuy = 0;
				String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
				Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
				if(!currencies.isEmpty()) {
					if(site.equalsIgnoreCase("ALL")) {
						for(String theSite : sites) {
							List<Transaction> transactions = siteTransactions.get(theSite);
							int siteSell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, startDate);
							int siteBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, startDate);
							printReportDay(theSite, period, siteSell, siteBuy, date, currencyCode);
							sumSiteSell += siteSell;
							sumSiteBuy += siteBuy;								
						}
						printReportPeriod(site, period, sumSiteSell, sumSiteBuy, currencyCode);
					}
					else {
						List<Transaction> transactions = siteTransactions.get(site);
						if(currencyCode.equalsIgnoreCase("ALL")) {
							List<String> currencyCodes = getAvailableCurrencyCodes(site, startDate, endDate);
							for(String currency : currencyCodes) {
								int siteSell = getStatisticsDay(site, transactions, currency, TransactionMode.SELL, startDate);
								int siteBuy = getStatisticsDay(site, transactions, currency, TransactionMode.BUY, startDate);
								printReportDay(site, period, siteSell, siteBuy, date, currency);
								sumSiteSell += siteSell;
								sumSiteBuy += siteBuy;	
							}
							printReportPeriod(site, period, sumSiteSell, sumSiteBuy, currencyCode);
						}
						else {
							int siteBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, startDate);
							int siteSell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, startDate);
							printReportDay(site, period, siteSell, siteBuy, date, currencyCode);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param site
	 * @param period
	 * @param currencyCode
	 * @param availableCurrencies
	 * @param startDate
	 * @param endDate
	 */
	public void printStatisticsWeek(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
			int sumSiteSell = 0, sumSiteBuy = 0;
			for(LocalDate date : dates) {
				int sumWeekSell = 0, sumWeekBuy = 0;
				String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
				Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
				if(!currencies.isEmpty()) {
					if(site.equalsIgnoreCase("ALL")) {
						for(String theSite : sites) {
							if(siteTransactions.containsKey(theSite)) {
								List<Transaction> transactions = siteTransactions.get(theSite);
								int siteSell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, date);
								int siteBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, date);
								printReportDay(theSite, period, siteSell, siteBuy, date, currencyCode);
								sumSiteSell += siteSell;
								sumSiteBuy += siteBuy;
								sumWeekSell += siteSell;
								sumWeekBuy += siteBuy;
								
							}
						}
						printReportPeriod(site, period, sumWeekSell, sumWeekBuy, currencyCode);
					}
					else {
						if(siteTransactions.containsKey(site)) {
							List<Transaction> transactions = siteTransactions.get(site);
							int siteBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, date);
							int siteSell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, date);
							printReportDay(site, period, siteSell, siteBuy, date, currencyCode);
							sumSiteSell += siteSell;
							sumSiteBuy += siteBuy;											
						}
					}
				}
			}
			printReportPeriod(site, period, sumSiteSell, sumSiteBuy, currencyCode);
		}
	}
	
	/**
	 * 
	 * @param site
	 * @param period
	 * @param currencyCode
	 * @param availableCurrencies
	 * @param startDate
	 * @param endDate
	 */
	public void printStatisticsMonth(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			int sumSiteSell = 0, sumSiteBuy = 0;
			if(site.equalsIgnoreCase("ALL")) {
				for(String theSite : sites) {
					if(siteTransactions.containsKey(theSite)) {
						List<Transaction> transactions = siteTransactions.get(theSite);
						int siteSell = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						int siteBuy = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						printReportPeriod(theSite, period, siteSell, siteBuy, currencyCode);
						sumSiteSell += siteSell;
						sumSiteBuy += siteBuy;
					}
				}
				printReportPeriod(site, period, sumSiteSell, sumSiteBuy, currencyCode);
			}
			else {
				if(siteTransactions.containsKey(site)) {
					List<Transaction> transactions = siteTransactions.get(site);
					if(currencyCode.equalsIgnoreCase("ALL")) {
						List<String> currencies = getAvailableCurrencyCodes(site, startDate, endDate); 
						for(String currency : currencies) {
							int siteSell = getStatisticsPeriod(site, transactions, currency, TransactionMode.SELL, startDate, endDate);
							int siteBuy = getStatisticsPeriod(site, transactions, currency, TransactionMode.BUY, startDate, endDate);
							printReportPeriod(site, period, siteSell, siteBuy, currency);
							sumSiteSell += siteSell;
							sumSiteBuy += siteBuy;						
						}						
						printReportPeriod(site, period, sumSiteSell, sumSiteBuy, currencyCode);
					}
					else {
						int siteSell = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						int siteBuy = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						printReportPeriod(site, period, siteSell, siteBuy, currencyCode);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param transactions
	 * @param currencyCode
	 * @param mode
	 * @param date
	 * @param currencies
	 * @return
	 */
	private int getStatisticsDay(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate date) {
		int statistics = 0;
		if(transactions != null){
			String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
			Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
			if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
				if(currencyCode.equals("ALL")) {
					List<String> currencyCodes = getAvailableCurrencyCodes(site, date, date);
					for(String currency : currencyCodes) {
						if(currencies.containsKey(currency)){
							float currencyRate = currencies.get(currency).getRate();
							statistics += (int)transactions
									.stream()
									.filter(cc -> cc.getCurrencyCode().equals(currency))
									.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
									.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
									.mapToDouble(t -> t.getAmount() * currencyRate)
									.sum();			
						}
					}
				}
				else {
					float currencyRate = currencies.get(currencyCode).getRate();
					statistics = (int)transactions
							.stream()
							.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
							.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
							.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
							.mapToDouble(t -> t.getAmount() * currencyRate)
							.sum();
				}
			}
		}
		return statistics;
	}
	
	/**
	 * 
	 * @param site
	 * @param transactions
	 * @param currencyCode
	 * @param mode
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private int getStatisticsPeriod(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate startDate, LocalDate endDate) {
		int statistics = 0;
		if(transactions != null){
			List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
			for(LocalDate date : dates) {
				String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
				Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
				if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
					if(currencyCode.equals("ALL")) {
						List<String> currencyCodes = getAvailableCurrencyCodes(site, startDate, endDate);
						for(String currency : currencyCodes) {
							if(currencies.containsKey(currency)){
								float currencyRate = currencies.get(currency).getRate();
								statistics += (int)transactions
										.stream()
										.filter(cc -> cc.getCurrencyCode().equals(currency))
										.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
										.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
										.mapToDouble(t -> t.getAmount() * currencyRate)
										.sum();										
							}
						}
					}
					else {
						float currencyRate = currencies.get(currencyCode).getRate();
						statistics += (int)transactions
								.stream()
								.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
								.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
								.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
								.mapToDouble(t -> t.getAmount() * currencyRate)
								.sum();
					}				
				}
			}
		}
		return statistics;
	}
	
	/**
	 * Print a report for a specific date
	 * @param site - the name for a Site or "ALL" for all Sites
	 * @param period - an enum for the chosen Period
	 * @param sell - an int holding the statistics for sell for the period
	 * @param buy - an int holding the statistics for buy for the period
	 * @param date - a specific date for the report
	 * @param currencyCode - the name of the specific currency or "ALL" for all currencies
	 */
	private static void printReportDay(String site, Period period, int sell, int buy, LocalDate date, String currencyCode) {
		System.out.format("Statistics for Site %s %s %s - Currency: %s%n", site.toUpperCase(), period.getName().toUpperCase(), date, currencyCode);
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy + " SEK");
		System.out.println("Profit " + (sell - buy) + " SEK");
		System.out.println();
	}
	
	/**
	 * Print a report for a specific period
	 * @param site - the name for a Site or "ALL" for all Sites
	 * @param period - an enum for the chosen Period
	 * @param sell - an int holding the statistics for sell for the period
	 * @param buy - an int holding the statistics for buy for the period
	 * @param currencyCode - the name of the specific currency or "ALL" for all currencies
	 */
	private static void printReportPeriod(String site, Period period, int sell, int buy, String currencyCode) {
		System.out.format("Statistics for Site %s - SUM PERIOD - Currency: %s%n", site.toUpperCase(), currencyCode);
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

	/**
	 * @return the sites
	 */
	public List<String> getSites() {
		return sites;
	}
	
	
}
