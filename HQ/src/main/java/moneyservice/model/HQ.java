package moneyservice.model;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;
import moneyservice.hq.app.HQApp;

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
	 * @attribute logger a Logger
	 */
	private static Logger logger;

	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}

	/**
	 * Constructor for HQ
	 * @param name - A String defining the name of HQ
	 * @param allTransactions - A Map defining all the transactions for each Site
	 */
	public HQ(String name, Map<String, List<Transaction>> allTransactions, List<String> sites) {
		this.name = name;
		this.siteTransactions = allTransactions;
		logger.fine("Map with all transactions is set!");
		this.sites = sites;
		logger.fine("List with all sites is set!");
	}
	
	/**
	 * Check if Site report matches the Transactions made from Box of Cash
 	 * @return boolean true if the Site report matches the Transactions made from Box of Cash
	 */
	public boolean checkCorrectnessSiteReport() {
		// get directory path for HQ project
		String HQdirPath = System.getProperty("user.dir");
		Map<String, Double> boxOfCash = Configuration.getBoxOfCash();
		// remove when all site reports are available
		List<String> temp = new ArrayList<>();
		temp.add("SOUTH");
		
		
		for(String site : temp) {
			// get Transaction directory path for each site
			String pathTransactions = HQdirPath + File.separator + Configuration.getPathTransactions() + site;
			// get Site Report directory path for each site
			String pathSiteReports = HQdirPath + File.separator + Configuration.getPathSiteReports();
			List<String> filesTransactions = HQApp.getFilenames(site, pathTransactions, ".ser");
			List<String> filesSiteReports = HQApp.getFilenames(site, pathSiteReports, ".txt");
			for(int i = 0 ; i < filesTransactions.size() ; i++) {
				try {
					String fileTransaction = filesTransactions.get(i);
					String fileSiteReport = filesSiteReports.get(i);
					String dateTransaction = fileTransaction.substring(fileTransaction.lastIndexOf("_")+1, fileTransaction.lastIndexOf("."));
					String dateSiteReport = fileSiteReport.substring(fileSiteReport.lastIndexOf("_")+1, fileSiteReport.lastIndexOf("."));
					if(dateTransaction.equals(dateSiteReport)) {
						List<Transaction> transactions = MoneyServiceIO.readReportAsSer(pathTransactions + File.separator + fileTransaction);
						Map<String, Double> siteReport = MoneyServiceIO.readSiteReport(pathSiteReports + fileSiteReport);
						for(String currency : boxOfCash.keySet()) {
							int sell = getStatisticsAmountDay(site, transactions, currency, TransactionMode.SELL, LocalDate.parse(dateTransaction));
							int buy = getStatisticsAmountDay(site, transactions, currency, TransactionMode.BUY, LocalDate.parse(dateTransaction));
							if(boxOfCash.containsKey(currency)) {
								if(!currency.equals(Configuration.getLOCAL_CURRENCY())) {
									double start = boxOfCash.get(currency);
									double end = siteReport.get(currency);
									if((start + buy - sell) != end) {
										return false;
									}									
								}
							}
							else {
								return false;
							}
						}
					}
				}
				catch(DateTimeParseException dte) {
					// logger.log(Level.WARNING, dte.getMessage());
					// System.out.println(dte.getMessage());
				}
			}
		}
		return true;
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
				logger.finer("ALL transactions has been set into Transactions List");
			}
			else {
				transactions = siteTransactions.get(key);
				logger.finer("Only Transactions from "+ key + "has been set into Transactions List");
			}
			// get all the available currency codes with no doubles
			availableCodes = transactions
					.stream()									// start a stream
					.filter(filterPeriod(startDate, endDate))
					.map(t -> t.getCurrencyCode())				// convert the stream to only handle currency codes
					.distinct()									// sort the currency code in alphabetic order
					.collect(Collectors.toList());				// collect all available element to a List
			logger.fine("All currency codes stored in availableCodes list");
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
				logger.finer("ALL transactions has been set into Transactions List");
			}
			else {
				transactions = siteTransactions.get(key);
				logger.finer("Only Transactions from "+ key + "has been set into Transactions List");
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
		return t -> (t.getTimeStamp().toLocalDate().isEqual(startDate) ||		// check if time stamp is equal to start date
					(t.getTimeStamp().toLocalDate().isAfter(startDate) &&		// or if it is after start date
					t.getTimeStamp().toLocalDate().isBefore(endDate)) ||		// and it is before end date
					t.getTimeStamp().toLocalDate().isEqual(endDate));
	};
	
	/**
	 * This method is for printing the statistics for Day report
	 * @param site - the name of the Site
	 * @param period - the chosen period for statistics
	 * @param currencyCode - the chosen currency for filter
	 * @param availableCurrencies - all the available currencies for the period
	 * @param startDate - the start date for period
	 * @param endDate - the end date (included) for the period
	 */
	public void printStatisticsDay(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
			for(LocalDate date : dates) {
				int sumDayAllSitesSell = 0;
				int sumDayAllSitesBuy = 0;
				String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
				Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
				if(!currencies.isEmpty()) {
					if(site.equalsIgnoreCase("ALL")) {
						for(String theSite : sites) {
							List<Transaction> transactions = siteTransactions.get(theSite);
							int siteSumDaySell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, startDate);
							int siteSumDayBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, startDate);
							printReportDay(theSite, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
							sumDayAllSitesSell += siteSumDaySell;
							sumDayAllSitesBuy += siteSumDayBuy;								
						}
						printReportPeriod(site, period, sumDayAllSitesSell, sumDayAllSitesBuy, currencyCode);
					}
					else {
						List<Transaction> transactions = siteTransactions.get(site);
						if(currencyCode.equalsIgnoreCase("ALL")) {
							for(String currency : availableCurrencies) {
								int siteSell = getStatisticsDay(site, transactions, currency, TransactionMode.SELL, startDate);
								int siteBuy = getStatisticsDay(site, transactions, currency, TransactionMode.BUY, startDate);
								printReportDay(site, period, siteSell, siteBuy, date, currency);
								sumDayAllSitesSell += siteSell;
								sumDayAllSitesBuy += siteBuy;	
							}
							printReportPeriod(site, period, sumDayAllSitesSell, sumDayAllSitesBuy, currencyCode);
						}
						else {
							int siteSumDaySell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, startDate);
							int siteSumDayBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, startDate);
							printReportDay(site, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method is for printing the statistics for Week report
	 * @param site - the name of the Site
	 * @param period - the chosen period for statistics
	 * @param currencyCode - the chosen currency for filter
	 * @param availableCurrencies - all the available currencies for the period
	 * @param startDate - the start date for period
	 * @param endDate - the end date (included) for the period
	 */
	public void printStatisticsWeek(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			List<LocalDate> dates = startDate.datesUntil(endDate.plusDays(1)).collect(Collectors.toList());
			int sumWeekAllSitesSell = 0, sumWeekAllSitesBuy = 0;
			for(LocalDate date : dates) {
				int sumDayAllSitesSell = 0, sumDayAllSitesBuy = 0;
				String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
				logger.fine(filename + " is set for parsing");
				Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
				if(!currencies.isEmpty()) {
					if(site.equalsIgnoreCase("ALL")) {
						logger.fine("Producing weekly statistics for ALL sites!");
						for(String theSite : sites) {
							if(siteTransactions.containsKey(theSite)) {
								List<Transaction> transactions = siteTransactions.get(theSite);
								int siteSumDaySell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, date);
								int siteSumDayBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, date);
								printReportDay(theSite, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
								sumWeekAllSitesSell += siteSumDaySell;
								sumWeekAllSitesBuy += siteSumDayBuy;
								sumDayAllSitesSell += siteSumDaySell;
								sumDayAllSitesBuy += siteSumDayBuy;
								
							}
						}
						printReportPeriod(site, period, sumDayAllSitesSell, sumDayAllSitesBuy, currencyCode);
					}
					else {
						if(siteTransactions.containsKey(site)) {
							List<Transaction> transactions = siteTransactions.get(site);
							int siteBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, date);
							int siteSell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, date);
							printReportDay(site, period, siteSell, siteBuy, date, currencyCode);
							sumWeekAllSitesSell += siteSell;
							sumWeekAllSitesBuy += siteBuy;											
						}
					}
				}
			}
			printReportPeriod(site, period, sumWeekAllSitesSell, sumWeekAllSitesBuy, currencyCode);
		}
	}
	
	/**
	 * This method is for printing the statistics for Month report
	 * @param site - the name of the Site
	 * @param period - the chosen period for statistics
	 * @param currencyCode - the chosen currency for filter
	 * @param availableCurrencies - all the available currencies for the period
	 * @param startDate - the start date for period
	 * @param endDate - the end date (included) for the period
	 */
	public void printStatisticsMonth(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			int sumAllSitesMonthSell = 0, sumAllSiteMonthBuy = 0;
			if(site.equalsIgnoreCase("ALL")) {
				for(String theSite : sites) {
					if(siteTransactions.containsKey(theSite)) {
						List<Transaction> transactions = siteTransactions.get(theSite);
						int siteTotalMonthSell = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						int siteTotalMonthBuy = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						printReportPeriod(theSite, period, siteTotalMonthSell, siteTotalMonthBuy, currencyCode);
						sumAllSitesMonthSell += siteTotalMonthSell;
						sumAllSiteMonthBuy += siteTotalMonthBuy;
					}
				}
				printReportPeriod(site, period, sumAllSitesMonthSell, sumAllSiteMonthBuy, currencyCode);
			}
			else {
				if(siteTransactions.containsKey(site)) {
					List<Transaction> transactions = siteTransactions.get(site);
					if(currencyCode.equalsIgnoreCase("ALL")) {
						for(String currency : availableCurrencies) {
							int siteSell = getStatisticsPeriod(site, transactions, currency, TransactionMode.SELL, startDate, endDate);
							int siteBuy = getStatisticsPeriod(site, transactions, currency, TransactionMode.BUY, startDate, endDate);
							printReportPeriod(site, period, siteSell, siteBuy, currency);
							sumAllSitesMonthSell += siteSell;
							sumAllSiteMonthBuy += siteBuy;						
						}						
						printReportPeriod(site, period, sumAllSitesMonthSell, sumAllSiteMonthBuy, currencyCode);
					}
					else {
						int siteTotalMonthSell = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						int siteTotalMonthBuy = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						printReportPeriod(site, period, siteTotalMonthSell, siteTotalMonthBuy, currencyCode);
					}
				}
			}
		}
	}
	
	/**
	 * This method is for getting the statistics for a specific day and TransactionMode
	 * @param site - name of the Site
	 * @param transactions - a List holding all Transactions
	 * @param currencyCode - a String with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode - the mode of the Transaction
	 * @param date - the specific date for the statistics
	 * @return an int holding the sum of local currency for the chosen parameters
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
							float currencyRate;
							if(mode.equals(TransactionMode.BUY)) {
								currencyRate = currencies.get(currency).getRate() * Configuration.getBuyRate();									
							}
							else {
								currencyRate = currencies.get(currency).getRate() * Configuration.getSellRate();
							}
							statistics += (int)transactions
									.stream()
									.filter(cc -> cc.getCurrencyCode().equals(currency))   	    //filters out the target currency
									.filter(cc -> cc.getMode().equals(mode))					// filter out only BUY orders (from user perspective)
									.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))	// or if it is equal to end date
									.mapToDouble(t -> t.getAmount() * currencyRate)				//multiples the amount with the current currencyRate
									.sum();														//sums everything
						}
					}
				}
				else {
					float currencyRate;
					if(mode.equals(TransactionMode.BUY)) {
						currencyRate = currencies.get(currencyCode).getRate() * Configuration.getBuyRate();									
					}
					else {
						currencyRate = currencies.get(currencyCode).getRate();
					}
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
	 * This method is for getting the statistics for a specific day and TransactionMode
	 * @param site - name of the Site
	 * @param transactions - a List holding all Transactions
	 * @param currencyCode - a String with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode - the mode of the Transaction
	 * @param date - the specific date for the statistics
	 * @return an int holding the sum of amount for the chosen parameters 
	 */
	private int getStatisticsAmountDay(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate date) {
		int statistics = 0;
		if(transactions != null){
			String filename = Configuration.getPathDailyRates() + "DETALJERAT RESULTAT_" + date.toString() + ".txt";
			Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
			if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
				if(currencyCode.equals("ALL")) {
					List<String> currencyCodes = getAvailableCurrencyCodes(site, date, date);
					for(String currency : currencyCodes) {
						if(currencies.containsKey(currency)){
							statistics += (int)transactions
									.stream()
									.filter(cc -> cc.getCurrencyCode().equals(currency))
									.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
									.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
									.mapToDouble(t -> t.getAmount())
									.sum();			
						}
					}
				}
				else {
					statistics = (int)transactions
							.stream()
							.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
							.filter(cc -> cc.getMode().equals(mode))				// filter out only BUY orders (from user perspective)
							.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))				// or if it is equal to end date
							.mapToDouble(t -> t.getAmount())
							.sum();
				}
			}
		}
		return statistics;
	}
	
	/**
	 * This function is for getting the statistics for a period and TransactionMode
	 * @param site - name of the Site
	 * @param transactions - a List holding all Transactions
	 * @param currencyCode - a String with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode - the mode of the Transaction
	 * @param startDate - the start date for period
	 * @param endDate - the end date (included) for the period
	 * @return an int holding the sum for the chosen parameters
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
							float currencyRate;
							if(currencies.containsKey(currency)){
								if(mode.equals(TransactionMode.BUY)) {
									currencyRate = currencies.get(currency).getRate() * Configuration.getBuyRate();									
								}
								else {
									currencyRate = currencies.get(currency).getRate() * Configuration.getSellRate();
								}
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
						float currencyRate;
						if(mode.equals(TransactionMode.BUY)) {
							currencyRate = currencies.get(currencyCode).getRate() * Configuration.getBuyRate();									
						}
						else {
							currencyRate = currencies.get(currencyCode).getRate();
						}
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
