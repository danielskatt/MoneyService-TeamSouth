package moneyservice.model;

import java.util.Locale;

public class Currency {
	// ATTRIBUTES
	private final String currencyCode;	
	private final float rate;			// how much 1 unit of foreign currency is worth in local currency
	
	// DEFAULT CONSTRUCTOR
	public Currency(String currencyCode, float rate) {
		this.currencyCode = currencyCode;
		this.rate = rate;
	}

	// GETTERS AND SETTERS
	public String getCurrencyCode() {
		return currencyCode;
	}

	public float getRate() {
		return rate;
	}
	
	// TOSTRING
	@Override
	public String toString() {
		return String.format(Locale.US, "Currency [currencyCode=%s, rate=%4f]", currencyCode, rate);
	}
}
