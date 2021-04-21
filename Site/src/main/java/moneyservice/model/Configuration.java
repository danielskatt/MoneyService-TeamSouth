package moneyservice.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.TreeMap;

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
	static LocalDate CURRENT_DATE;
	
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
	 * Parses the information in the configuration file sent from application
	 * Stores the filename from available currencies and their rates 
	 * and read the box of cash for the Site
	 * @param filename - Name of the configuration file
	 */
	public static void parseConfigFile(String filename) {
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
					case "CurrencyConfig":
						currencyConfigFile = value;
						break;
					case "ReferenceCurrency":
						LOCAL_CURRENCY = value;
						break;
					default:
						if(key.length() == 3 && key.matches("^[A-Z]*$")) {
							double cash = (double)Integer.parseInt(value);
							boxOfCash.putIfAbsent(key, cash);
						}
						break;
					}
				}
			}
		}
		catch(IOException ioe) {
			// TODO - Replace printout with adding information to LOG-FILE
			System.out.println(ioe.getMessage());
		}
		catch(NumberFormatException e) {
			// TODO - Replace printout with adding information to LOG-FILE
			System.out.println(e.getMessage());
		}
		currencies = parseCurrencyFile(currencyConfigFile);
	}
	
	/**
	 * Parse information from Currency COnfiguration file with all the available 
	 * currencies read from the system
	 * @param filename - Name of the Currency configuration file
	 * @return A map with all the available currencies read from file
	 */
	private static Map<String, Currency> parseCurrencyFile(String filename){
		Map<String, Currency> temp = new TreeMap<String, Currency>();
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			String date = filename.substring(filename.indexOf("_")+1, filename.indexOf("."));
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
			System.out.println(ioe.getMessage());
		}
		catch(NumberFormatException e) {
			System.out.println(e.getMessage());
		}
		catch(DateTimeParseException dte) {
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
	
}
