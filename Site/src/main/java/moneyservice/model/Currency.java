package moneyservice.model;

import java.util.Locale;


/**--------Currency Class---------
 * The currency class is a value type, used 
 * to store the ticker of a currency and a
 * it's exchange rate.
 */

public class Currency {
	
	/**
	 * @attribute currencyCode - Holds the information of a currency ticker ex. USD,EUR.
	 */
	private final String currencyCode;
	
	/**
	 * @attribute rate - Stores a the exchange rate of a specific currency.
	 */
	private final float rate;			

	/**
	 * Overloaded Constructor for Currency - takes in parameters for a currency ticker and exchange rate.
	 * @param currencyCode - ticker of a currency, rate - exchange rate of a specific currency.
	 */
	public Currency(String currencyCode, float rate) throws IllegalArgumentException {
		// If currencyCode are longer than 3 characters throw exception and don't create object
		if(currencyCode.length() == 3 && currencyCode.matches("^[A-Z]*$")) {
			// Has to be upper case so we force it
			currencyCode = currencyCode.toUpperCase();
			this.currencyCode = currencyCode;
		} else {
			throw new IllegalArgumentException("currencyCode not valid! supplied currencyCode: "+currencyCode);
		}

		this.rate = rate;
	}

	/**
	 * Returns the ticker of a specific currency.
	 * @return String - currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Returns the exchange rate of a specific currency.
	 * @return rate - the exchange rate.
	 */
	public float getRate() {
		return rate;
	}

	/**
	 * Returns the String in format ex. Currency [currencyCode=EUR, rate=10.16]
	 * @return String in the above mentioned format.
	 */
	@Override
	public String toString() {
		return String.format(Locale.US, "Currency [currencyCode=%s, rate=%4f]", currencyCode, rate);
	}
	
}
