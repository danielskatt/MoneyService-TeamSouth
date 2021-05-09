package moneyservice.model;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;
import moneyservice.hq.app.HQApp;

/**
 * This class holds all Sites available and handles all statistics
 */
public class HQ {

	/**
	 * name a String defining the name of HQ
	 */
	private final String name;

	/**
	 * siteTransactions a {@code Map<String, List<Transaction>>} holding all the transactions for each Site
	 */
	private final Map<String, List<Transaction>> siteTransactions;

	/**
	 * sites a {@code List<String>} holding all the available sites
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
	 * Default constructor for HQ
	 * @param name a String defining the name of HQ
	 * @param allTransactions a {@code Map<String, List<Transaction>>} holding all the transactions for each Site
	 * @param sites a {@code List<String>} holding all the available sites
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
			List<String> filesTransactions = HQApp.getFilenames(pathTransactions, ".ser");
			List<String> filesSiteReports = HQApp.getFilenames(pathSiteReports, ".txt");
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
										logger.log(Level.WARNING, "The total left amount between boxOfCash and daily siteReport does not correspond!");
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
					logger.log(Level.SEVERE, dte.getMessage());
				}
			}
		}
		return true;
	}

	/**
	 * A method to get all the currency codes in the List of Transactions between two dates
	 * @param key a String holding information about which Site to get the currency codes for
	 * @param startDate a LocalDate holding start date with format YYYY-MM-DD
	 * @param endDate a LocalDate holding end date with format YYYY-MM-DD
	 * @return a {@code List<String>} with all available currency codes
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
					.stream()									// start a stream
					.filter(filterPeriod(startDate, endDate))
					.map(t -> t.getCurrencyCode())				// convert the stream to only handle currency codes
					.distinct()									// sort the currency code in alphabetic order
					.collect(Collectors.toList());				// collect all available element to a List
		}
		return availableCodes;
	}

	/**
	 * A method to print all the transactions for the chosen Site
	 * @param key a String holding information about which Site to get the currency codes for
	 * @param period a Period (enum) defining the chosen period for statistics
	 * @param startDate a LocalDate holding start date with format YYYY-MM-DD
	 * @param endDate a LocalDate holding end date with format YYYY-MM-DD
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
	 * @param startDate a LocalDate holding the start date for the period
	 * @param endDate a LocalDate holding the end date (included) for the period
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
	 * @param site a String holding the name of the Site
	 * @param period a Period (enum) defining the chosen period for statistics
	 * @param currencyCode a String defining the chosen currency for filter
	 * @param availableCurrencies a {@code List<String>} holding all the available currencies for the period
	 * @param startDate a LocalDate holding the start date for period
	 */
	public void printStatisticsDay(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			List<String> filenames = HQApp.getFilenames(Configuration.getPathDailyRates(), "txt");
			for(String filename : filenames) {
				try {
					String eachDate = filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."));
					LocalDate date = LocalDate.parse(eachDate);
					if(date.isEqual(startDate)) {
						int sumDayAllSitesSell = 0, sumDayAllSitesBuy = 0;
						int sumDayAllCurrenciesSell = 0, sumDayAllCurrenciesBuy = 0; 
						Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
						if(!currencies.isEmpty()) {
							if(site.equalsIgnoreCase("ALL")) {
								for(String theSite : sites) {
									List<Transaction> transactions = siteTransactions.get(theSite);
									int siteSumDaySell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, startDate);
									logger.fine("Total daily amount of sell for: "+ theSite + " during: " + startDate + " of currency: " +  currencyCode + ": " +siteSumDaySell+" "+ Configuration.getLOCAL_CURRENCY());
									int siteSumDayBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, startDate);
									logger.fine("Total daily amount of buy for: "+ theSite + " during: " + startDate + " of currency: " +  currencyCode+ ": " + siteSumDayBuy+" "+Configuration.getLOCAL_CURRENCY());
									printReportDay(theSite, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
									sumDayAllSitesSell += siteSumDaySell;
									sumDayAllSitesBuy += siteSumDayBuy;
									logger.fine("Total daily amount of sell for all Sites: "+ sumDayAllSitesSell +" "+ Configuration.getLOCAL_CURRENCY() +" during: "+ startDate + " of currency: " +  currencyCode );
									logger.fine("Total daily amount of buy for all Sites: "+ sumDayAllSitesBuy +" "+Configuration.getLOCAL_CURRENCY()+ " during: "+ startDate + " of currency: " +  currencyCode );
								}
								printReportPeriod(site, period, sumDayAllSitesSell, sumDayAllSitesBuy, currencyCode);
							}
							else {
								List<Transaction> transactions = siteTransactions.get(site);
								if(currencyCode.equalsIgnoreCase("ALL")) {
									for(String currency : availableCurrencies) {
										int siteSumDaySell = getStatisticsDay(site, transactions, currency, TransactionMode.SELL, startDate);
										logger.fine("Total daily amount of sell of: "+currency +" for site: "+ site + " during:" + startDate.toString() +" : "+siteSumDaySell+ Configuration.getLOCAL_CURRENCY());
										int siteSumDayBuy = getStatisticsDay(site, transactions, currency, TransactionMode.BUY, startDate);
										logger.fine("Total daily amount of buy of: "+currency+" for site: "+ site + " during:" + startDate.toString() +" : "+siteSumDayBuy + Configuration.getLOCAL_CURRENCY());
										printReportDay(site, period, siteSumDaySell, siteSumDayBuy, date, currency);
										sumDayAllCurrenciesSell += siteSumDaySell;
										sumDayAllCurrenciesBuy += siteSumDayBuy;	
									}
									logger.fine("Total daily amount sell of all currencies for site: "+site+" during: "+ startDate.toString() + " : "+sumDayAllCurrenciesSell +" "+Configuration.getLOCAL_CURRENCY());
									logger.fine("Total daily amount pbuy of all currencies for site: "+site+" during: "+ startDate.toString() + " : "+sumDayAllCurrenciesBuy +" "+Configuration.getLOCAL_CURRENCY());
									printReportPeriod(site, period, sumDayAllCurrenciesSell, sumDayAllCurrenciesBuy, currencyCode);
								}
								else {
									int siteSumDaySell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, startDate);
									logger.fine("Total daily amount sell for site: "+ site + " during: "+ startDate.toString() + " of " +currencyCode +": "+ siteSumDaySell+" "+ Configuration.getLOCAL_CURRENCY());
									int siteSumDayBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, startDate);
									logger.fine("Total daily amount buy for site: "+ site + " during: "+ startDate.toString() + " of " +currencyCode +": "+ siteSumDayBuy+" "+ Configuration.getLOCAL_CURRENCY());
									printReportDay(site, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
								}
							}
						}					
					}
				}
				catch(DateTimeParseException dtpe) {

				}
			}
		}
	}

	/**
	 * This method is for printing the statistics for Week report
	 * @param site a String defining the name of the Site
	 * @param period a Period (enum) defining the chosen period for statistics
	 * @param currencyCode a String defining the chosen currency for filter
	 * @param availableCurrencies a {@code List<String>} holding all the available currencies for the period
	 * @param startDate a LocalDate defining the start date for period
	 * @param endDate a LocalDate defining the end date (included) for the period
	 */
	public void printStatisticsWeek(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			boolean fileFound = false;
			List<String> filenames = HQApp.getFilenames(Configuration.getPathDailyRates(), "txt");
			int sumWeekAllSitesSell = 0, sumWeekAllSitesBuy = 0;
			for(String filename : filenames) {
				try {
					String eachDate = filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."));
					LocalDate date = LocalDate.parse(eachDate);
					if(date.isEqual(startDate) || (date.isAfter(startDate) && date.isBefore(endDate) || date.isEqual(endDate))) {
						int sumDayAllSitesSell = 0, sumDayAllSitesBuy = 0;
						Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
						logger.fine(filename + " is set for parsing");
						if(!currencies.isEmpty()) {
							if(site.equalsIgnoreCase("ALL")) {
								logger.fine("Producing weekly statistics for ALL sites!");
								for(String theSite : sites) {
									if(siteTransactions.containsKey(theSite)) {
										List<Transaction> transactions = siteTransactions.get(theSite);
										int siteSumDaySell = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.SELL, date);
										logger.fine("Total daily amount of sell for: "+ theSite + " during: " + date.toString() + " of currency:" +  currencyCode + " :" +siteSumDaySell+" "+ Configuration.getLOCAL_CURRENCY());
										int siteSumDayBuy = getStatisticsDay(theSite, transactions, currencyCode, TransactionMode.BUY, date);
										logger.fine("Total daily amount of buy for: "+ theSite + " during: " + date.toString() + " of currency:" +  currencyCode+ " :" + siteSumDayBuy+" "+ Configuration.getLOCAL_CURRENCY());
										printReportDay(theSite, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
										sumWeekAllSitesSell += siteSumDaySell;
										sumWeekAllSitesBuy += siteSumDayBuy;
										sumDayAllSitesSell += siteSumDaySell;
										sumDayAllSitesBuy += siteSumDayBuy;
									}
								}
								logger.fine("Total daily amount sell for all sites of currency:"+currencyCode+" during: "+date.toString()+ " Amount: "+sumDayAllSitesSell +" "+Configuration.getLOCAL_CURRENCY());
								logger.fine("Total daily amount buy for all sites of currency:"+currencyCode+" during: "+ date.toString() + " Amount: "+sumDayAllSitesBuy +" "+Configuration.getLOCAL_CURRENCY());
								printReportPeriod(site, period, sumDayAllSitesSell, sumDayAllSitesBuy, currencyCode);
							}
							else {
								if(siteTransactions.containsKey(site)) {
									List<Transaction> transactions = siteTransactions.get(site);
									int siteSumDaySell = getStatisticsDay(site, transactions, currencyCode, TransactionMode.SELL, date);
									logger.fine("Total daily amount of sell of currency: "+currencyCode +" for site: "+ site + " during:" + date.toString() +" : "+siteSumDaySell+" "+ Configuration.getLOCAL_CURRENCY());
									int siteSumDayBuy = getStatisticsDay(site, transactions, currencyCode, TransactionMode.BUY, date);
									logger.fine("Total daily amount of buy of currency: "+ currencyCode+" for site: "+ site + " during:" + date.toString() +" : "+siteSumDayBuy +" "+Configuration.getLOCAL_CURRENCY());
									printReportDay(site, period, siteSumDaySell, siteSumDayBuy, date, currencyCode);
									sumWeekAllSitesSell += siteSumDaySell;
									sumWeekAllSitesBuy += siteSumDayBuy;
									logger.fine("Total daily amount sell for "+site+" site of currency:"+currencyCode+" during: "+date.toString()+ " Amount: "+sumDayAllSitesSell +" "+Configuration.getLOCAL_CURRENCY());
									logger.fine("Total daily amount buy for "+site+" site of currency:"+currencyCode+" during: "+ date.toString() + " Amount: "+sumDayAllSitesBuy +" "+Configuration.getLOCAL_CURRENCY());
								}
							}
							fileFound = true;						
						}
					}
				}
				catch(DateTimeParseException dtpe) {

				}
			}
			if(fileFound) {
				printReportPeriod(site, period, sumWeekAllSitesSell, sumWeekAllSitesBuy, currencyCode);					
			}

			logger.fine("Total weekly amount of buy for "+site+" site of currency:"+currencyCode+ " during:"
					+ startDate.toString()+"-"+endDate.toString() +" Amount: "+sumWeekAllSitesBuy + " "+Configuration.getLOCAL_CURRENCY());

			logger.fine("Total weekly amount of sell for "+site+" site of currency:"+currencyCode+" during:" 
					+ startDate +"-"+endDate.toString()+" Amount: "+sumWeekAllSitesSell + " "+Configuration.getLOCAL_CURRENCY());
		}
	}

	/**
	 * This method is for printing the statistics for Month report
	 * @param site a String defining the name of the Site
	 * @param period a Period (enum) defining the chosen period for statistics
	 * @param currencyCode a String defining the chosen currency for filter
	 * @param availableCurrencies a {@code List<String>} holding all the available currencies for the period
	 * @param startDate a LocalDate defining the start date for period
	 * @param endDate a LocalDate defining the end date (included) for the period
	 */
	public void printStatisticsMonth(String site, Period period, String currencyCode, List<String> availableCurrencies, LocalDate startDate, LocalDate endDate) {
		if(siteTransactions.containsKey(site) || site.equalsIgnoreCase("ALL")) {
			int sumAllSitesMonthSell = 0, sumAllSiteMonthBuy = 0;
			if(site.equalsIgnoreCase("ALL")) {
				logger.fine("Producing monthly statistics for ALL sites!");
				for(String theSite : sites) {
					if(siteTransactions.containsKey(theSite)) {
						List<Transaction> transactions = siteTransactions.get(theSite);
						int siteTotalMonthSell = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						logger.fine("Total amount of sell for site: "+ theSite + " during: " + startDate.toString() +"-"+ endDate.toString()+" for currency:" +  currencyCode + " Amount:" + siteTotalMonthSell+" "+ Configuration.getLOCAL_CURRENCY());
						int siteTotalMonthBuy = getStatisticsPeriod(theSite, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						logger.fine("Total amount of buy for site: "+ theSite + " during: " + startDate.toString() +"-"+endDate.toString()+" for currency:" +  currencyCode+ " Amount:" + siteTotalMonthBuy+" "+ Configuration.getLOCAL_CURRENCY());
						printReportPeriod(theSite, period, siteTotalMonthSell, siteTotalMonthBuy, currencyCode);
						sumAllSitesMonthSell += siteTotalMonthSell;
						sumAllSiteMonthBuy += siteTotalMonthBuy;
					}
				}
				logger.fine("Total amount sell for all sites of currency "+currencyCode+" during: "+ startDate.toString() +"-"+ endDate.toString()+
						" Amount: "+sumAllSitesMonthSell +" "+Configuration.getLOCAL_CURRENCY());
				logger.fine("Total monthly amount buy for currency "+currencyCode+"  during: "+ startDate.toString() +"-"+endDate.toString()+ 
						" Amount: "+sumAllSiteMonthBuy +" "+Configuration.getLOCAL_CURRENCY());
				printReportPeriod(site, period, sumAllSitesMonthSell, sumAllSiteMonthBuy, currencyCode);
			}
			else {
				if(siteTransactions.containsKey(site)) {
					List<Transaction> transactions = siteTransactions.get(site);
					if(currencyCode.equalsIgnoreCase("ALL")) {
						for(String currency : availableCurrencies) {
							int siteSumMonthSell = getStatisticsPeriod(site, transactions, currency, TransactionMode.SELL, startDate, endDate);
							logger.fine("Total amount of sell for site: "+ site + " during: " + startDate.toString() +"-"+ endDate.toString()+
									" for currency:" +  currencyCode + " Amount:" +siteSumMonthSell+" "+ Configuration.getLOCAL_CURRENCY());

							int siteSumMonthBuy = getStatisticsPeriod(site, transactions, currency, TransactionMode.BUY, startDate, endDate);
							logger.fine("Total amount of buy for site: "+ site + " during: " + startDate.toString() +"-"+endDate.toString()+
									" for currency:" +  currencyCode+ " Amount:" + siteSumMonthBuy+" "+ Configuration.getLOCAL_CURRENCY());

							printReportPeriod(site, period,siteSumMonthSell, siteSumMonthBuy, currency);
							sumAllSitesMonthSell += siteSumMonthSell;
							sumAllSiteMonthBuy += siteSumMonthBuy;						
						}		

						logger.fine("Total amount sell for "+site+" during period:"+startDate.toString()+"-"+endDate.toString()+" of all currencies:"+sumAllSitesMonthSell +" "+Configuration.getLOCAL_CURRENCY());
						logger.fine("Total amount buy for "+site+" during period:"+startDate.toString()+"-"+endDate.toString()+" of all currencies:"+sumAllSiteMonthBuy +" "+Configuration.getLOCAL_CURRENCY());

						printReportPeriod(site, period, sumAllSitesMonthSell, sumAllSiteMonthBuy, currencyCode);
					}
					else {
						int siteTotalMonthSell = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.SELL, startDate, endDate);
						logger.fine("Total amount sell for"+ site + "during period: "+startDate.toString()+"-"+endDate.toString()+ " of " +currencyCode +": "+ siteTotalMonthSell+" "+Configuration.getLOCAL_CURRENCY());

						int siteTotalMonthBuy = getStatisticsPeriod(site, transactions, currencyCode, TransactionMode.BUY, startDate, endDate);
						logger.fine("Total  amount purchase for: "+ site + " during period:"+startDate.toString()+"-"+endDate.toString()+" of " +currencyCode +": "+ siteTotalMonthBuy+" "+Configuration.getLOCAL_CURRENCY());

						printReportPeriod(site, period, siteTotalMonthSell, siteTotalMonthBuy, currencyCode);
					}
				}
			}
		}
	}

	/**
	 * This method is for getting the statistics for a specific day and TransactionMode
	 * @param site a String defining name of the Site
	 * @param transactions a {@code List<Transactions>} holding all Transactions
	 * @param currencyCode a String with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode a TransactionMode defining the mode of the Transaction
	 * @param date a LocalDate defining the specific date for the statistics
	 * @return an int holding the sum of local currency for the chosen parameters
	 */
	private int getStatisticsDay(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate date) {
		int statistics = 0;
		if(transactions != null){
			List<String> filenames = HQApp.getFilenames(Configuration.getPathDailyRates(), "txt");
			for(String filename : filenames) {
				String eachDate = filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."));
				if(eachDate.equals(date.toString())) {
					Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
					if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
						if(currencyCode.equals("ALL")) {
							List<String> currencyCodes = getAvailableCurrencyCodes(site, date, date);
							for(String currency : currencyCodes) {
								if(currencies.containsKey(currency)){
									logger.finer("Now producing statistic for: "+ currency);
									float currencyRate;
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
							logger.finer("Producing statistics for currency code: "+ currencyCode);
							float currencyRate;
							if(mode.equals(TransactionMode.BUY)) {
								currencyRate = currencies.get(currencyCode).getRate() * Configuration.getBuyRate();									
							}
							else {
								currencyRate = currencies.get(currencyCode).getRate();
							}
							statistics += (int)transactions
									.stream()
									.filter(cc -> cc.getCurrencyCode().equals(currencyCode))   	    //filters out the target currency
									.filter(cc -> cc.getMode().equals(mode))					// filter out only BUY orders (from user perspective)
									.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))	// or if it is equal to end date
									.mapToDouble(t -> t.getAmount() * currencyRate)				//multiples the amount with the current currencyRate
									.sum();														//sums everything
						}
					}
					else{
						logger.log(Level.WARNING, "Currency code could not be found!");
					}	
				}
			}
		}
		return statistics;

	}

	/**
	 * This method is for getting the statistics for a specific day and TransactionMode
	 * @param site a String defining the name of the Site
	 * @param transactions a {@code List<String>} holding all Transactions
	 * @param currencyCode a String defining with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode a TransactionMode (enum) defining the mode of the Transaction
	 * @param date a LocalDate defining the specific date for the statistics
	 * @return an int holding the sum of amount for the chosen parameters 
	 */
	private int getStatisticsAmountDay(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate date) {
		int statistics = 0;
		if(transactions != null){
			List<String> filenames = HQApp.getFilenames(Configuration.getPathDailyRates(), "txt");
			for(String filename : filenames){
				String eachDate = filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."));
				if(eachDate.equals(date.toString())) {
					Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
					if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
						if(currencyCode.equals("ALL")) {
							List<String> currencyCodes = getAvailableCurrencyCodes(site, date, date);
							for(String currency : currencyCodes) {
								if(currencies.containsKey(currency)){
									logger.finer("Now producing statistic for: "+ currency);
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
							logger.finer("Producing statistics for currency code: "+ currencyCode);
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
			}
		}
		return statistics;
	}

	/**
	 * This function is for getting the statistics for a period and TransactionMode
	 * @param site a String holding name of the Site
	 * @param transactions a {@code List<Transaction>} holding all Transactions
	 * @param currencyCode a String with the chosen currency or "ALL" for a summary of all currencies
	 * @param mode a TransactionMode (enum) defining the mode of the Transaction
	 * @param startDate a LocalDate defining the start date for period
	 * @param endDate a LocalDate defining the end date (included) for the period
	 * @return an int holding the sum for the chosen parameters
	 */
	private int getStatisticsPeriod(String site, List<Transaction> transactions, String currencyCode, TransactionMode mode, LocalDate startDate, LocalDate endDate) {
		int statistics = 0;
		if(transactions != null){
			List<String> filenames = HQApp.getFilenames(Configuration.getPathDailyRates(), "txt");
			for(String filename : filenames) {
				try {
					String eachDate = filename.substring(filename.lastIndexOf("_")+1, filename.lastIndexOf("."));
					LocalDate date = LocalDate.parse(eachDate);
					if(date.isEqual(startDate) || (date.isAfter(startDate) && date.isBefore(endDate) || date.isEqual(endDate))) {
						Map<String, Currency> currencies = Configuration.parseCurrencyFile(filename);
						if(currencies.containsKey(currencyCode) || currencyCode.equals("ALL")) {
							if(currencyCode.equals("ALL")) {
								List<String> currencyCodes = getAvailableCurrencyCodes(site, startDate, endDate);
								for(String currency : currencyCodes) {
									logger.finer("Now producing statistic for: "+ currency);
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
												.filter(cc -> cc.getCurrencyCode().equals(currency))		//filters out the target currency
												.filter(cc -> cc.getMode().equals(mode))					// filter out only BUY orders (from user perspective)
												.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))	// or if it is equal to end date
												.mapToDouble(t -> t.getAmount() * currencyRate)				//multiples the amount with the current currencyRate
												.sum();											
									}
								}
							}
							else {
								float currencyRate;
								logger.finer("Producing statistics for currency code: "+ currencyCode);
								if(mode.equals(TransactionMode.BUY)) {
									currencyRate = currencies.get(currencyCode).getRate() * Configuration.getBuyRate();									
								}
								else {
									currencyRate = currencies.get(currencyCode).getRate();
								}
								statistics += (int)transactions
										.stream()
										.filter(cc -> cc.getCurrencyCode().equals(currencyCode))		//filters out the target currency
										.filter(cc -> cc.getMode().equals(mode))					// filter out only BUY orders (from user perspective)
										.filter(t -> t.getTimeStamp().toLocalDate().isEqual(date))	// or if it is equal to end date
										.mapToDouble(t -> t.getAmount() * currencyRate)				//multiples the amount with the current currencyRate
										.sum();														//sums everything
							}
						}

					}
				}
				catch(DateTimeParseException dtpe){

				}
			}
		}
		return statistics;
	}

	/**
	 * Print a report for a specific date
	 * @param site a String defining the name for a Site or "ALL" for all Sites
	 * @param period a Period (enum) for the chosen Period
	 * @param sell an int holding the statistics for sell for the period
	 * @param buy an int holding the statistics for buy for the period
	 * @param date a LocalDate defining a specific date for the report
	 * @param currencyCode a String defining the name of the specific currency or "ALL" for all currencies
	 */
	private static void printReportDay(String site, Period period, int sell, int buy, LocalDate date, String currencyCode) {
		System.out.format("Statistics for Site %s %s %s - Currency: %s%n", site.toUpperCase(), period.getName().toUpperCase(), date, currencyCode);
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy + " SEK");
		System.out.println("Profit " + (sell - buy) + " SEK");
		logger.fine("Profit " + (sell - buy) + " SEK");
		System.out.println();
	}

	/**
	 * Print a report for a specific period
	 * @param site a String defining the name for a Site or "ALL" for all Sites
	 * @param period a Period (enum) defining the chosen Period
	 * @param sell an int holding the statistics for sell for the period
	 * @param buy an int holding the statistics for buy for the period
	 * @param currencyCode a String defining the name of the specific currency or "ALL" for all currencies
	 */
	private static void printReportPeriod(String site, Period period, int sell, int buy, String currencyCode) {
		System.out.format("Statistics for Site %s - SUM PERIOD - Currency: %s%n", site.toUpperCase(), currencyCode);
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy + " SEK");
		System.out.println("Profit " + (sell - buy) + " SEK");
		logger.fine("Total profit " + (sell - buy) + " SEK");
		System.out.println();
	}

	/**
	 * Getter for attribute name
	 * @return name a String defining the name of HQ
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for attribute siteTransactions
	 * @return siteTransactions a {@code Map<String, List<Transaction>>} holding all the transactions for each Site
	 */
	public Map<String, List<Transaction>> getSiteTransactions() {
		return siteTransactions;
	}

	/**
	 * Getter for attribute sites
	 * @return sites a {@code List<String>} holding all the available sites
	 */
	public List<String> getSites() {
		return sites;
	}
}
