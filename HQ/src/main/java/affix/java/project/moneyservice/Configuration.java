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
	 * @attribute - CURRENT_DATE - The current Date in ISO standard
	 */
	static LocalDate CURRENT_DATE = LocalDate.now();
	
	/**
	 * @attribute boxOfCash - Holds information about the box of cash that will be delivered to Site
	 */
	static Map<String, Double> boxOfCash;
	/**
	 * @attribute currencies - Holds information about all available currencies and their rates read from a file
	 */
	static Map<String, Currency> currencies;
	/**
	 * @attribute logger
	 */
	private static Logger logger;
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
	
//	static{
//		logger = Logger.getLogger("affix.java.project.moneyservice");
//	}
	
	/**
	 * Parses the information in the configuration file sent from application
	 * Stores the filename from available currencies and their rates 
	 * and read the box of cash for the Site
	 * @param filename - Name of the configuration file
	 */
	public static boolean parseConfigFile(String filename) {
		boxOfCash = new TreeMap<String, Double>();
		currencies = new TreeMap<String, Currency>();
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			while(br.ready()) {
				String eachLine = br.readLine();
				String[] parts = eachLine.split("=");
				if(parts.length == 2) {
					String key = parts[0].strip();
					String value = parts[1].strip();
					
					switch(key) {
					case "Sites":
						String theSites = value.substring(value.indexOf("{")+1, value.lastIndexOf("}"));
						String[] allSites = theSites.split(",");
						for(String site : allSites) {
							sites.add(site.strip());
						}
						break;
					case "ReferenceCurrency":
						if(value.length() == 3 && value.matches("^[A-Z]*$")) {
							LOCAL_CURRENCY = value;							
						}
						else {
							//TODO: FIX LOGGING
//							logger.finest(key + " cannot have reference currency as " + value);
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
								//TODO: FIX LOGGING
//								logger.finest(value + " is invalid");
							}
						}
						break;
					}
				}
			}
		}
		catch(IOException ioe) {
			// logger.log(Level.WARNING, "Error occured while reading from "+ filename);
			System.out.println(ioe.getMessage());
			return false;
		}
		
		if(LOCAL_CURRENCY == null) {
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
		// logger.info("Reading currency rates from " + filename);
		
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
			// logger.log(Level.WARNING, ioe.getMessage());
			// System.out.println(ioe.getMessage());
		}
		catch(NumberFormatException e) {
			// logger.log(Level.WARNING, e.getMessage());
			// System.out.println(e.getMessage());
		}
		catch(DateTimeParseException dte) {
			// logger.log(Level.WARNING, dte.getMessage());
			// System.out.println(dte.getMessage());
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
	 * @return the cURRENT_DATE
	 */
	public static LocalDate getCURRENT_DATE() {
		return CURRENT_DATE;
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
