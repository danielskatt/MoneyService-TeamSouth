package affix.java.project.moneyservice;

import java.io.BufferedReader;
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
	static String LOCAL_CURRENCY;		// TODO: create setter?					
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

					case "currencyconfig":	// TODO: hardcoded cases?
						if(!value.isEmpty()) {
							currencyConfigFile = "DailyRates/" + value;		// TODO: refactor?
						}
						else {
							logger.finest("Invalid configuration format, currency configuration file: " +eachLine);		// TODO: throw exception or set to default?
						}
						break;

					case "referencecurrency":
						if(value.length() == 3 && value.matches("^[A-Z]*$")) {
							LOCAL_CURRENCY = value;							
						}
						else {
							logger.finest("Invalid configuration format, local currency: " +eachLine);		// TODO: throw exception or set to default?
						}

						break;

					case "logformat":
						value = value.toLowerCase();	// convert value to lower case to minimize typo error

						switch(value) {
						case "text":
							logFormat = value;
							break;

						case "xml":
							logFormat = value;
							break;

						default:
							System.out.println("Invalid configuration format, log format: " +eachLine);
							logger.finest("Invalid configuration format, log format: " +eachLine);
							logger.finest("Log format is set to default value: " +logFormat);
							break;
						}

						break;

					case "loglevel":
						try {
							logLevel = Level.parse(value);
						}
						catch (IllegalArgumentException e) {
							System.out.println("Invalid configuration format, log level: " +eachLine);
							logger.finest("Invalid configuration format, log level: " +eachLine);
							logger.finest("Log level is set to default value: " +logLevel.toString());
						}

						break;

					case "testmode":
						value = value.toLowerCase();	// convert value to lower case to minimize typo error

						if(value.equals("true")) {
							testMode = true;
						}
						else if(value.equals("true")) {
							testMode = false;
						}
						else {
							System.out.println("Invalid configuration format, test mode: " +eachLine);
							logger.finest("Invalid configuration format, test mode: " +eachLine);
							logger.finest("Test mode is set to default value: " +testMode);
						}

						break;

					case "transactionfee":
						try {
							TRANSACTION_FEE = Float.parseFloat(value);
						}
						catch (NumberFormatException e) {
							System.out.println("Invalid configuration format, transaction fee: " +eachLine);
							logger.finest("Invalid configuration format, transaction fee: " +eachLine);
							logger.finest("Transaction fee is set to default value: " +TRANSACTION_FEE);
						}
						break;

					default:	// currency in Box of Cash or invalid configuration format
						if(key.length() == 3 && key.matches("^[A-Z]*$")) {	// key is currency and value is amount for that currency
							try {
								double cash = (double)Integer.parseInt(value);								
								boxOfCash.putIfAbsent(key, cash);
							}
							catch(NumberFormatException e) {
								System.out.println("Invalid configuration format: " +eachLine);
								logger.finest("Invalid configuration format: " +eachLine);
							}
						}
						else {	// invalid configuration key
							System.out.println("Invalid configuration format: " +eachLine);
							logger.finest("Invalid configuration format: " +eachLine);
						}

						break;
					}
				}
			}
		}
		catch(IOException ioe) {
			// TODO - Replace printout with adding information to LOG-FILE
			logger.log(Level.WARNING, "Error occured while reading from "+ filename);
			System.out.println(ioe.getMessage());
			return false;
		}

		if(currencyConfigFile == null || LOCAL_CURRENCY == null) {
			return false;
		}
		else {
			currencies = parseCurrencyFile(currencyConfigFile);
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
			logger.log(Level.WARNING, ioe.getMessage());
			System.out.println(ioe.getMessage());
		}
		catch(NumberFormatException e) {
			logger.log(Level.WARNING, e.getMessage());
			System.out.println(e.getMessage());
		}
		catch(DateTimeParseException dte) {
			logger.log(Level.WARNING, dte.getMessage());
			System.out.println(dte.getMessage());
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

}
