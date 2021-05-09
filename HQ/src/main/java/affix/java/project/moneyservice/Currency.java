package affix.java.project.moneyservice;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class defines a Currency object in MoneyService
 * The currency class is a value type used to store the currency code of a currency 
 * and it's exchange rate without interest.
 * The currency code must be in capital letters. 
 */
public class Currency {
	
	/**
	 * logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static {logger = Logger.getLogger("affix.java.project.moneyservice");}
	
	/**
	 * currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 */
	private final String currencyCode;
	
	/**
	 * rate a float holding the exchange rate of the currency
	 */
	private final float rate;
	

	/**
	 * Default constructor for creating a complete Currency object
	 * @param currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 * @param rate a float holding the exchange rate of the currency
	 * @throws IllegalArgumentException if parameters don't match requirements 
	 */
	public Currency(String currencyCode, float rate) throws IllegalArgumentException {
		
		// If currencyCode are longer than 3 characters or lower case throw exception 
		if(currencyCode.length() == 3 && currencyCode.matches("^[A-Z]*$")) {

			this.currencyCode = currencyCode;	
		} 
		else {
			logger.log(Level.SEVERE, currencyCode + " as currencyCode is not valid!");
			throw new IllegalArgumentException("Invalid input for Currency!");

		}

		this.rate = rate;
	}

	
	/**
	 * Getter for attribute currencyCode
	 * @return currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Getter for attribute rate
	 * @return rate a float holding the exchange rate of the currency
	 */
	public float getRate() {
		return rate;
	}

	/**
	 * Converting object data to human readable format
	 * @return a String using format {@code "Currency [currencyCode=<currencyCode>, rate=<rate>]"}
	 */
	@Override
	public String toString() {
		return String.format(Locale.US, "Currency [currencyCode=%s, rate=%.4f]", currencyCode, rate);
	}
}
