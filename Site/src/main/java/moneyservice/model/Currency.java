package moneyservice.model;

public class Currency {
	// ATTRIBUTES
	private final String currencyCode;	
	private final float rate;			// how much 1 unit of foreign currency is worth in local currency
	
	// DEFAULT CONSTRUCTOR
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
		return String.format("Currency [currencyCode=%s, rate=%f]", currencyCode, rate);
	}
}
