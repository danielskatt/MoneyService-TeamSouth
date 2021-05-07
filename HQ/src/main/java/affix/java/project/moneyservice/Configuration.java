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

/** 
 * Configuration for Money Service HQ application. 
 * Values should be supplied in a text file using key = value pattern. 
 * The values is set to a default value if no key = value pair is supplied in the text file.
 * NB! key = value pair for attributes listed below needs to be supplied for the application to work:
 * - {@code List<String>} defining all sites 
 * - LOCAL_CURRENCY defining code for the local currency
 * - boxOfCash key = value pair for currency codes that is supported and the amount of the currency
 */
public class Configuration {

	/**
	 * TRANSACTION_FEE a float defining the transaction fee the Site will charge the customer.
	 * Default value is set to 0.005
	 */
	static final float TRANSACTION_FEE = 0.005F;
	
	/**
	 * LOCAL_CURRENCY a String holding the code of the local currency the Site will trade with
	 */
	static String LOCAL_CURRENCY;							
	
	/**
	 * SELL_RATE a float calculated from transaction fee defining the sell rate for Site 
	 */
	static final float SELL_RATE = 1 + TRANSACTION_FEE;
	
	/**
	 * BUY_RATE a float calculated from transaction fee defining the buy rate for Site
	 */
	static final float BUY_RATE = 1 - TRANSACTION_FEE;
	
	/**
	 * boxOfCash a {@code Map<String, Double>} holding information about the box of cash that will be delivered to Site.
	 * A String holding the code of the currency (three capital letters) and amount of each currency.
	 */
	static Map<String, Double> boxOfCash;
	
	/**
	 * currencies a {@code Map<String, Currency>}  holding information about all available currencies and their rates 
	 * A String holding the code of the currency (three capital letters) and corresponding Currency object.
	 */
	static Map<String, Currency> currencies;
	
	/**
	 * sites a {@code List<String>} defining all sites
	 */
	static List<String> sites = new ArrayList<String>();
	
	/**
	 * CURRENT_DATE a LocalDate defining the current Date in ISO standard (YYYY-MM-DD)
	 */
	static LocalDate CURRENT_DATE = LocalDate.now();
	
	/**
	 * pathDailyRates a String defining directory path containing files for currency rates.<p>
	 * Format {"DirectoryName/"}. Default format {"DailyRates/"}
	 */
	static String pathDailyRates = "DailyRates";
	
	/**
	 * pathOrders a String holding the directory path containing files for orders.<p>
	 * Format {"DirectoryName/"}. Default format {"Orders/"}
	 */
	static String pathOrders = "Orders";
	
	/**
	 * pathSiteReports a String holding the directory path containing files for site reports. <p>
	 * Format {"DirectoryName/"}. Default format {"SiteReports/"}
	 */
	static String pathSiteReports = "SiteReports";
	
	/**
	 * pathTransactions a String holding the directory path containing files for transactions. <p>
	 * Format {"DirectoryName/"}. Default format {"Transactions/"}
	 */
	static String pathTransactions = "Transactions";
	
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
	 * This method parses the information in the configuration file sent from application
	 * and sets the configuration values that the file contains. 
	 * @param filename a String holding name of the configuration file including path
	 * @return boolean if the configuration was successful
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
					

					switch(key.toLowerCase()) {
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
						break;

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
					
					case "sites":

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
					case "referencecurrency":
						if(value.length() == 3 && value.matches("^[A-Z]*$")) {
							LOCAL_CURRENCY = value;							
						}
						else {

							logger.log(Level.SEVERE,"Invalid configuration format, reference currency: " +eachLine);
							return false;
						}
						break;
								
					case "pathtransactions":
						pathTransactions = value + File.separator;
						break;
					case "pathorders":
						pathOrders = value + File.separator;
						break;
					case "pathdailyrates":
						pathDailyRates = value + File.separator;
						break;
					case "pathsitereports":
						pathSiteReports = value + File.separator;
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
		if(sites.isEmpty()) {
			return false;
		}
		if(boxOfCash.isEmpty()) {
			return false;
		}
		return true;
	}
	
	/**
	 * Parse information from Currency Configuration file with all the available 
	 * currencies read from the system
	 * @param filename a String holding name of the currency file including path
	 * @return temp a {@code Map<String, Currency>} with all the available currencies and rates read from file
	 */
	public static Map<String, Currency> parseCurrencyFile(String filename){
		Map<String, Currency> temp = new TreeMap<String, Currency>();
		 
		
		filename = Configuration.getPathDailyRates() + filename;
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
			 logger.log(Level.SEVERE,ioe.getMessage()); 
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
	 * Getter for attribute TRANSACTION_FEE
	 * @return TRANSACTION_FEE a float defining the transaction fee the Site will charge the customer.
	 * Default value is set to 0.005
	 */
	public static float getTransactionFee() {
		return TRANSACTION_FEE;
	}

	/**
	 * Getter for attribute LOCAL_CURRENCY
	 * @return LOCAL_CURRENCY a String holding the code of the local currency the Site will trade with
	 */
	public static String getLOCAL_CURRENCY() {
		return LOCAL_CURRENCY;
	}

	/**
	 * Getter for attribute SELL_RATE
	 * @return SELL_RATE a float calculated from transaction fee defining the sell rate for Site 
	 */
	public static float getSellRate() {
		return SELL_RATE;
	}

	/**
	 * Getter for attribute BUY_RATE
	 * @return BUY_RATE a float calculated from transaction fee defining the buy rate for Site
	 */
	public static float getBuyRate() {
		return BUY_RATE;
	}
	
	/**
	 * Getter for attribute CURRENT_DATE
	 * @return CURRENT_DATE a LocalDate defining the current Date in ISO standard (YYYY-MM-DD)
	 */
	public static LocalDate getCURRENT_DATE() {
		return CURRENT_DATE;
	}

	/**
	 * Getter for attribute boxOfCash
	 * @return boxOfCash a {@code Map<String, Double>} holding information about the box of cash that will be delivered to Site.
	 * A String holding the code of the currency (three capital letters) and amount of each currency.
	 */
	public static Map<String, Double> getBoxOfCash() {
		return boxOfCash;
	}

	/**
	 * Getter for attribute sites
	 * @return sites a {@code List<String>} defining all sites
	 */
	public static List<String> getSites() {
		return sites;
	}

	/**
	 * Getter for attribute pathDailyRates
	 * @return pathDailyRates a String defining directory path containing files for currency rates.<p>
	 * Format {"DirectoryName/"}. Default format {"DailyRates/"}
	 */
	public static String getPathDailyRates() {
		return pathDailyRates;
	}

	/**
	 * Getter for attribute pathOrders
	 * @return pathOrders a String holding the directory path containing files for orders.<p>
	 * Format {"DirectoryName/"}. Default format {"Orders/"}
	 */
	public static String getPathOrders() {
		return pathOrders;
	}

	/**
	 * Getter for attribute pathSiteReports
	 * @return pathSiteReports a String holding the directory path containing files for site reports. <p>
	 * Format {"DirectoryName/"}. Default format {"SiteReports/"}
	 */
	public static String getPathSiteReports() {
		return pathSiteReports;
	}

	/**
	 * Getter for attribute pathTransactions
	 * @return pathTransactions a String holding the directory path containing files for transactions. <p>
	 * Format {"DirectoryName/"}. Default format {"Transactions/"}
	 */
	public static String getPathTransactions() {
		return pathTransactions;
	}
}
