package affix.java.project.moneyservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/** ------------------- Configuration (Configurator) ----------------------
 * <p>
 *  Holds the information to configure the application.
 *  The information will be set once the application sends the configuration
 *  file to the parseConfigFile() method
 * <p>
 * -----------------------------------------------------------------------*/
public class Configuration {

	/**
	 * @attribute logger a Logger
	 */
	private static Logger logger;

	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}

	/**
	 * @attribute TRANSACTION_FEE - Holds information about the fee the Site will charge the User for each successful Order
	 */
	static float TRANSACTION_FEE = 0.005F;
	/**
	 * @attribute - LOCAL_CURRENCY - Holds information about which currency the Site will trade with
	 */
	static String LOCAL_CURRENCY;			
	/**
	 * @attribute - SELL_RATE - A helper attribute for calculating the rate for selling a currency from a User
	 */
	static final float SELL_RATE = 1 + TRANSACTION_FEE;
	/**
	 * @attribute - BUY_RATE - A helper attribute for calculating the rate for buying a currency from a User
	 */
	static final float BUY_RATE = 1 - TRANSACTION_FEE;
	/**
	 * @attribute - CURRENT_DATE - The current Date in ISO standard
	 */
	static LocalDate CURRENT_DATE;		// TODO: create setter?	if not test mode, current date is today

	/**
	 * @attribute currencyConfigFile - Holds the name of the currency configuration file <CurrencyConfig_<Date in ISO standard>.txt>
	 */
	private static String currencyConfigFile;
	/**
	 * @attribute boxOfCash - Holds information about the box of cash that will be delivered to Site
	 */
	static Map<String, Double> boxOfCash;
	/**
	 * @attribute currencies - Holds information about all available currencies and their rates read from a file
	 */
	static Map<String, Currency> currencies;

	/**
	 * @attribute logFormat a String defining the format of log file, txt or xml. Default value is text file.
	 */
	static String logFormat = "text";

	/**
	 * @attribute logLevel a Level defining the level of logging. Log levels: info, all, warning, fine, finer or finest. 
	 * Default logLevel is set to all.
	 */
	static Level logLevel = Level.ALL;

	/**
	 * @attribute testMode a boolean defining if application should be run in test mode or normal mode
	 * Default testMode is set to false.
	 */
	static boolean testMode = false;

	/**
	 * @attribute siteName a String (upper case) defining the name of the Site.
	 */
	static String siteName;		
	
	/**
	 * @attribute pathTransactions a String holding the directory path for storing transactions.<p>
	 * Format {"DirectoryName/"}. Default format {"Transactions/"}
	 */
	static String pathTransactions = "Transactions" + File.separator;
	
	/**
	 * @attribute pathDailyRates a String holding the directory path for retrieving daily rates files (currencies).<p>
	 * Format {"DirectoryName/"}. Default format {"DailyRates/"}
	 */
	static String pathDailyRates = "DailyRates" + File.separator;	
	
	/**
	 * @attribute pathConfigs a String holding the directory path for retrieving configuration file.<p>
	 * Format {"DirectoryName/"}. Default format {"Configs/"}
	 */
	static String pathConfigs = "Configs" + File.separator;	
	
	/**
	 * @attribute pathOrders a String holding the directory path for storing orders.<p>
	 * Format {"DirectoryName/"}. Default format {"Orders/"}
	 */
	static String pathOrders = "Orders" + File.separator;	
	
	/**
	 * @attribute pathSiteReports a String holding the directory path for storing site reports
	 */
	static String pathSiteReports = "SiteReports" + File.separator;
	
	/**
	 * @attribute fileNameSiteReport a String holding path and file name for daily Site report.<p>
	 * Format: {@code"<pathSiteReports>/SiteReport_<SiteName>_<YYYY-MM-DD>.txt"}
	 */
	static String fileNameSiteReport;
	
	/**
	 * @attribute fileNameTransactionsReport a String holding path and file name for daily transactions report.<p>
	 * Format: {@code"<pathTransactions>/<SiteName>/Report_<SiteName>_<YYYY-MM-DD>.ser"}
	 */
	static String fileNameTransactionsReport;
	
	/**
	 * @attribute fileNameOrdersReport a String holding path and file name for daily orders report.<p>
	 * Format: {@code"<pathOrders>/Orders_<YYYY-MM-DD>.txt"}
	 */
	static String fileNameOrdersReport;
	
//	/**
//	 * @attribute fileNameCurrencyConfig a String holding path and file name for daily currency configuration file.<p>
//	 * Format: {@code"<pathDailyRates>/<fileName.txt>"}
//	 */
//	static String fileNameCurrencyConfig;
	
	/*--- Methods -----------------------------------------------------------------*/

	/**
	 * Parses the information in the configuration file sent from application
	 * Stores the filename from available currencies and their rates 
	 * and read the box of cash for the Site
	 * @param filename - Name of the configuration file
	 */
	public static boolean parseConfigFile(String filename) {

		boxOfCash = new TreeMap<String, Double>();
		currencies = new TreeMap<String, Currency>();

		try(BufferedReader br = new BufferedReader(new FileReader(filename))) {

			while(br.ready()) {
				String eachLine = br.readLine();
				String[] parts = eachLine.split("=");

				if(parts.length == 2) {
					String key = parts[0].strip();
					String value = parts[1].strip();

					// Set up configuration parameters
					switch(key.toLowerCase()) {		// convert key to lower case to minimize typo error
					case "currencyconfig":
						if(!value.isEmpty()) {
							currencyConfigFile = value;	
						}
						else {
							logger.log(Level.SEVERE, "Invalid configuration format, currency config file is empty: "+eachLine);	
						}
						break;

					case "referencecurrency":
						if(value.length() == 3 && value.matches("^[A-Z]*$")) {
							LOCAL_CURRENCY = value;							
						}
						else {
							logger.log(Level.SEVERE,"Invalid configuration format, local currency: " +eachLine);
						}

						break;

					case "logformat":
						value = value.toLowerCase();	// convert value to lower case to minimize typo error

						switch(value) {
						case "text":
						case "xml":
							logFormat = value;
							logger.fine("Current logformat is set to: "+ value);
							break;

						default:
							logger.log(Level.WARNING,"Invalid configuration format, log format: " +eachLine);
							logger.fine("Log format is set to default value: " +logFormat);
							break;
						}

						break;

					case "loglevel":
						try {
							logLevel = Level.parse(value);
							logger.fine("Current loglevel is set to: "+ value);
						}
						catch (IllegalArgumentException e) {
							logger.log(Level.WARNING,"Invalid configuration format, log level: " +eachLine);
							logger.fine("Log level is set to default value: " +logLevel.toString());
						}

						break;

					case "testmode":
						value = value.toLowerCase();	// convert value to lower case to minimize typo error

						if(value.equals("true")) {
							testMode = true;
						}
						else if(value.equals("false")) {
							testMode = false;
						}
						else {
							logger.log(Level.WARNING,"Invalid configuration format, test mode: " +eachLine);
							logger.fine("Test mode is set to default value: " +testMode);
						}

						break;

					case "transactionfee":
						try {
							TRANSACTION_FEE = Float.parseFloat(value);
						}
						catch (NumberFormatException e) {
							logger.log(Level.WARNING,"Invalid configuration format, transaction fee: " +eachLine);
							logger.fine("Transaction fee is set to default value: " +TRANSACTION_FEE);
						}
						break;
						
					case "sitename":
						if(!value.isEmpty()) {
							siteName = value.toUpperCase();	
						}
						else {
							logger.log(Level.SEVERE, "Invalid configuration format, site name is empty: "+eachLine);	
						}
	
						break;
						
					case "pathtransactions":
						if(!value.isEmpty()) {
							pathTransactions = value;
						}
						else {
							logger.log(Level.WARNING, "Invalid configuration format, path transactions: "+eachLine);
							logger.fine("Path for transactions is set to default value: " +pathTransactions);
						}
						break;
						
//					case "pathdailyrates":
//						if(!value.isEmpty()) {
//							pathDailyRates = value;
//						}
//						else {
//							logger.log(Level.WARNING, "Invalid configuration format, path daily rates (currencies): " +eachLine);
//							logger.log(Level.WARNING, "Path for daily rates (currencies) is set to default value: " +pathDailyRates);
//						}
//						break;
					case "pathconfigs":
						if(!value.isEmpty()) {
							pathConfigs = value;
						}
						else {
							logger.log(Level.WARNING, "Invalid configuration format, path configs: " +eachLine);
							logger.fine("Path for configuration is set to default value: " +pathConfigs);
						}
						break;
						
					case "pathorders":
						if(!value.isEmpty()) {
							pathConfigs = value;
						}
						else {
							logger.log(Level.WARNING, "Invalid configuration format format, path orders: " +eachLine);
							logger.fine("Path for orders is set to default value: " +pathOrders);
						}
						break;
						
					default:	// currency in Box of Cash or invalid configuration format
						if(key.length() == 3 && key.matches("^[A-Z]*$")) {	// key is currency and value is amount for that currency
							try {
								double cash = (double)Integer.parseInt(value);								
								boxOfCash.putIfAbsent(key, cash);
							}
							catch(NumberFormatException e) {
								logger.log(Level.WARNING,"Invalid configuration format for value: " +eachLine);
							}
						}
						else {	// invalid configuration key
							logger.log(Level.WARNING,"Invalid configuration format for key: " +eachLine);
						}

						break;
					}
				}
			}
		}
		catch(IOException ioe) {
			logger.log(Level.SEVERE, "Error occured while reading from: "+ filename);
			return false;
		}

		if(currencyConfigFile == null || LOCAL_CURRENCY == null || siteName == null) {
			logger.log(Level.SEVERE, "Error occured while trying to set Config Params!"); // TODO: fix this soon
			return false;
		}
		else {
			currencies = parseCurrencyFile(currencyConfigFile);
			if(currencies.isEmpty()) {
				logger.log(Level.SEVERE, "Currencies map is empty!");
				return false;
			}
			
			setFileNamePaths();		// set all file names
		}

		return true;
	}

	/**
	 * Parse information from Currency COnfiguration file with all the available 
	 * currencies read from the system
	 * @param filename - Name of the Currency configuration file
	 * @return A map with all the available currencies read from file
	 */
	private static Map<String, Currency> parseCurrencyFile(String filename){
		Map<String, Currency> temp = new TreeMap<String, Currency>();
		logger.info("Reading currency rates from " + filename);

		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String date = filename.substring(filename.indexOf("_")+1, filename.lastIndexOf("."));
			CURRENT_DATE = LocalDate.parse(date);
			while(br.ready()) {
				String eachLine = br.readLine();
				String parts[] = eachLine.split("\\s+");
				if(parts.length == 8) {
					int amount = Integer.parseInt(parts[5].strip());
					String currencyCode = parts[6].strip();
					float rate = Float.parseFloat(parts[7].strip());
					float rateSameReference = (float) rate / amount;
					Currency currency = new Currency(currencyCode, rateSameReference);

					temp.putIfAbsent(currencyCode, currency);
				}
			}
		}
		catch(IOException ioe) {
			logger.log(Level.SEVERE, ioe.toString());
		}
		catch(NumberFormatException e) {
			logger.log(Level.SEVERE, e.toString());
		}
		catch(DateTimeParseException dte) {
			logger.log(Level.SEVERE, dte.toString());
		}

		return temp;
	}
	
	/**
	 * Helper method for setting file name with path for directory included
	 */
	private static void setFileNamePaths() {
		
		// change siteName from upper case to first upper and rest lower
		String siteNametemp = siteName.substring(0, 1).toUpperCase() + siteName.substring(1).toLowerCase(); 
		
		// Format: {@code"<pathSiteReports>/SiteReport_<SiteName>_YYYY-MM-DD.txt"}
		fileNameSiteReport = pathSiteReports + "SiteReport_" + siteNametemp + "_" + getCURRENT_DATE().toString()  + ".txt";
		logger.finer(fileNameSiteReport + " is set as folder for Site Reports");
		
		// Format: {@code"<pathTransactions>/<SiteName>/Report_<SiteName>_<YYYY-MM-DD>.ser"}
		fileNameTransactionsReport = pathTransactions + siteName.toUpperCase() + File.separator + "Report_" + siteName + "_" + getCURRENT_DATE().toString() + ".ser";
		logger.finer(fileNameTransactionsReport + " is set as folder for Transaction Reports");
		
		// Format: {@code"<pathOrders>/Orders_<YYYY-MM-DD>.txt"}
		fileNameOrdersReport = pathOrders + "Orders_" + getCURRENT_DATE().toString()  + ".txt";
		logger.finer(fileNameOrdersReport + " is set as folder for Order Report");
		
//		// Format: {@code"<pathDailyRates>/<fileName.txt>"}
//		fileNameCurrencyConfig = pathDailyRates + currencyConfigFile;
	}

	/*--- Getters -----------------------------------------------------------------*/
	/**
	 * @return the transactionFee
	 */
	public static float getTransactionFee() {
		return TRANSACTION_FEE;
	}

	/**
	 * @return the lOCAL_CURRENCY
	 */
	public static String getLOCAL_CURRENCY() {
		return LOCAL_CURRENCY;
	}

	/**
	 * @return the sellRate
	 */
	public static float getSellRate() {
		return SELL_RATE;
	}

	/**
	 * @return the buyRate
	 */
	public static float getBuyRate() {
		return BUY_RATE;
	}

	/**
	 * @return the cURRENT_DATE
	 */
	public static LocalDate getCURRENT_DATE() {
		return CURRENT_DATE;
	}

	/**
	 * @return the currencyConfigFile
	 */
	public static String getCurrencyConfigFile() {
		return currencyConfigFile;
	}

	/**
	 * @return the boxOfCash
	 */
	public static Map<String, Double> getBoxOfCash() {
		return boxOfCash;
	}

	/**
	 * @return the currencies
	 */
	public static Map<String, Currency> getCurrencies() {
		return currencies;
	}

	/**
	 * Getter for attribute logFormat
	 * @return logFormat a String defining the format of log file, txt or xml. Default value is text file.
	 */
	public static String getLogFormat() {
		return logFormat;
	}

	/**
	 * Getter for attribute logLevel
	 * @return logLevel a Level defining the level of logging. Log levels: info, all, warning, fine, finer or finest. 
	 * Default log level is set to all.
	 */
	public static Level getLogLevel() {
		return logLevel;
	}

	/**
	 * Getter for attribute testMode
	 * @return testMode a boolean defining if application should be run in test mode or normal mode
	 * Default testMode is set to false.
	 */
	public static boolean isTestMode() {
		return testMode;
	}

	/**
	 * Getter for attribute siteName
	 * @return siteName a String (upper case) defining the name of the Site
	 */
	public static String getSiteName() {
		return siteName;
	}
	
	/**
	 * Getter for attribute pathTransactions
	 * @return pathTransactions a String holding the directory path for storing transactions
	 */
	public static String getPathTransactions() {
		return pathTransactions;
	}

	/**
	 * Getter for attribute pathDailyRates
	 * @return pathDailyRates a String holding the directory path for retrieving daily rates files (currencies)
	 */
	public static String getPathDailyRates() {
		return pathDailyRates;
	}

	/**
	 * Getter for attribute pathConfigs
	 * @return pathConfigs a String holding the directory path for retrieving configuration file
	 */
	public static String getPathConfigs() {
		return pathConfigs;
	}

	/**
	 * Getter for attribute pathOrders
	 * @return pathOrders a String holding the directory path for storing orders
	 */
	public static String getPathOrders() {
		return pathOrders;
	}

	/**
	 * Getter for attribute pathSiteReports
	 * @return pathSiteReports a String holding the directory path for storing site reports
	 */
	public static String getPathSiteReports() {
		return pathSiteReports;
	}

	/**
	 * Getter for attribute fileNameSiteReport
	 * @return fileNameSiteReport a String holding path and file name for daily Site report.<p>
	 * Format: {@code"<pathSiteReports>/SiteReport_<SiteName>_<YYYY-MM-DD>.txt"}
	 */
	public static String getFileNameSiteReport() {
		return fileNameSiteReport;
	}

	/**
	 * Getter for attribute fileNameTransactionsReport
	 * @return fileNameTransactionsReport a String holding path and file name for daily transactions report.<p>
	 * Format: {@code"<pathTransactions>/<SiteName>/Report_<SiteName>_<YYYY-MM-DD>.ser"}
	 */
	public static String getFileNameTransactionsReport() {
		return fileNameTransactionsReport;
	}

	/**
	 * Getter for attribute fileNameOrdersReport
	 * @return fileNameOrdersReport a String holding path and file name for daily orders report.<p>
	 * Format: {@code"<pathOrders>/Orders_<YYYY-MM-DD>.txt"}
	 */
	public static String getFileNameOrdersReport() {
		return fileNameOrdersReport;
	}

//	/**
//	 * Getter for attribute fileNameCurrencyConfig
//	 * @return fileNameCurrencyConfig a String holding path and file name for daily currency configuration file.<p>
//	 * Format: {@code"<pathDailyRates>/<fileName.txt>"}
//	 */
//	public static String getFileNameCurrencyConfig() {
//		return fileNameCurrencyConfig;
//	}

}
