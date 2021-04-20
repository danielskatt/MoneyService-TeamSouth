package moneyservice.model;

/*-------------Currency Class-------------
 * Value type class used to store different currency codes 
 * and their exchange rates.
 * 
 */
public class Currency {
	
	/**
	 * @attribute currencyCode - Holds the information of a currency ticker ex. USD,EUR.
	 */
	private final String currencyCode;
	
	/**
	 * @attribute rate - Holds the information of the exchange rate for a specific currency.
	 */
	private final float rate;	
	
	/**
	 * Constructor for Currency - takes in parameters for a currency ticker and exchange rate.
	 * @param currencyCode - ticker of a currency, rate - exchange rate of a specific currency.
	 */
	public Currency(String currencyCode, float rate) {
		this.currencyCode = currencyCode;
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
		return String.format("Currency [currencyCode=%s, rate=%f]", currencyCode, rate);
	}
}
