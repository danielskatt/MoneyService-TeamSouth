package affix.java.project.moneyservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

/** ------------------- Configuration (Configurator) ----------------------
 * <p>
 *  Holds the information to configure the application.
 *  The information will be set once the application sends the configuration
 *  file to the parseConfigFile() method
 * <p>
 * -----------------------------------------------------------------------*/
public class Configuration {

	/**
	 * @attribute TRANSACTION_FEE - Holds information about the fee the Site will charge the User for each successful Order
	 */
	static final float TRANSACTION_FEE = 0.005F;
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
	 * @attribute sites
	 */
	static List<String> sites = new ArrayList<String>();
	/**
	 * @attribute pathDailyRates
	 */
	static String pathDailyRates = "DailyRates" + File.separator;
	/**
	 * @attribute pathOrders
	 */
	static String pathOrders = "Orders" + File.separator;
	/**
	 * @attribute pathSiteReports
	 */
	static String pathSiteReports = "SiteReports" + File.separator;
	/**
	 * @attribute pathTransactions
	 */
	static String pathTransactions = "Transactions" + File.separator;
	
	/**
	 * fh a FileHandler
	 */
	private static FileHandler fh;
	
	/**
	 * @attribute logger
	 */
	private static Logger logger;
	
	/**
	 * @attribute logFormat a String defining the format of log file, txt or xml. Default value is text file.
	 */
	static String logFormat = "text";
	
	/**
	 * @attribute logLevel a Level defining the level of logging. Log levels: info, all, warning, fine, finer or finest. 
	 * Default logLevel is set to all.
	 */
	static Level logLevel = Level.ALL;
	
	static{
		logger = Logger.getLogger("affix.java.project.moneyservice");
	}
	
	public static void setFileHandler() {
		try {	
			// choose formatter for logging output text/xml
			if(logFormat.equals("text")){
				fh = new FileHandler("HQLogging_" + LocalDate.now() + ".txt");	
				fh.setFormatter(new SimpleFormatter()); // maybe add logging for logformat here?
			}
			else{
				fh = new FileHandler("HQLogging_" + LocalDate.now() + ".xml");	
				fh.setFormatter(new XMLFormatter());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.addHandler(fh);
		logger.setLevel(logLevel);
	}
	
	/**
	 * Parses the information in the configuration file sent from application
	 * Stores the filename from available currencies and their rates 
	 * and read the box of cash for the Site
	 * @param filename - Name of the configuration file
	 */
	public static boolean parseConfigFile(String filename) {
		boxOfCash = new TreeMap<String, Double>();
		currencies = new TreeMap<String, Currency>();
		setFileHandler(); // here temporarily, will be moved later
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			while(br.ready()) {
				String eachLine = br.readLine();
				String[] parts = eachLine.split("=");
				if(parts.length == 2) {
					String key = parts[0].strip();
					String value = parts[1].strip();
					
					switch(key) {
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
							logger.log(Level.WARNING,"Log format is set to default value: " +logFormat);
							break;
						}

					case "loglevel":
						try {
							logLevel = Level.parse(value);
							logger.fine("Current loglevel is set to: "+ value);
						}
						catch (IllegalArgumentException e) {
							logger.log(Level.WARNING,"Invalid configuration format, log level: " +eachLine);
							logger.log(Level.WARNING,"Log level is set to default value: " +logLevel.toString());
						}
						
						break;
					case "Sites":
						String theSites = value.substring(value.indexOf("{")+1, value.lastIndexOf("}"));
						String[] allSites = theSites.split(",");
						if(allSites.length > 0) {
							for(String site : allSites) {
								sites.add(site.strip());
							}
						}
						else {
							logger.log(Level.SEVERE,"Invalid configuration format, site is empty: " +eachLine);
							return false;
						}
						
						break;
					case "ReferenceCurrency":
						if(value.length() == 3 && value.matches("^[A-Z]*$")) {
							LOCAL_CURRENCY = value;							
						}
						else {
							logger.log(Level.SEVERE,"Invalid configuration format, reference currency: " +eachLine);
							return false;
						}
						break;
						
					case "PathTransactions":
						pathTransactions = value;
						break;
					case "PathOrders":
						pathOrders = value;
						break;
					case "PathDailyRates":
						pathDailyRates = value;
						break;
					case "PathSiteReports":
						pathSiteReports = value;
						break;
					default:
						if(key.length() == 3 && key.matches("^[A-Z]*$")) {
							try {
								double cash = (double)Integer.parseInt(value);								
								boxOfCash.putIfAbsent(key, cash);
							}
							catch(NumberFormatException e) {
								logger.log(Level.WARNING,"Invalid configuration format for value: " +eachLine);
							}
						}
						break;
					}
				}
			}
		}
		catch(IOException ioe) {
			logger.log(Level.SEVERE, "Error occured while reading from "+ filename);
			return false;
		}
		
		if(LOCAL_CURRENCY == null) {
			logger.log(Level.SEVERE, "Error occured while trying to set Config Params!");
			return false;
		}
		
		return true;
	}
	
	/**
	 * Parse information from Currency COnfiguration file with all the available 
	 * currencies read from the system
	 * @param filename - Name of the Currency configuration file
	 * @return A map with all the available currencies read from file
	 */
	public static Map<String, Currency> parseCurrencyFile(String filename){
		Map<String, Currency> temp = new TreeMap<String, Currency>();
		 
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
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
			 //logger.finest(ioe.getMessage()); //TODO: Uncomment this later
		}
		catch(NumberFormatException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		catch(DateTimeParseException dte) {
			 logger.log(Level.SEVERE, dte.getMessage());
		}
		
		return temp;
	}

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
	 * @return the sites
	 */
	public static List<String> getSites() {
		return sites;
	}

	/**
	 * @return the pathDailyRates
	 */
	public static String getPathDailyRates() {
		return pathDailyRates;
	}

	/**
	 * @return the pathOrders
	 */
	public static String getPathOrders() {
		return pathOrders;
	}

	/**
	 * @return the pathSiteReports
	 */
	public static String getPathSiteReports() {
		return pathSiteReports;
	}

	/**
	 * @return the pathTransactions
	 */
	public static String getPathTransactions() {
		return pathTransactions;
	}
	
	
	
}
