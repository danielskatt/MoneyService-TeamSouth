package affix.java.project.moneyservice;

import java.util.logging.Logger;

/** 
 * This class defines a Order in MoneyService
 * Order should be created by specifying site name, currency code, the amount and 
 * transaction mode, buy or sell. The order is created by a customer of the Money Service 
 * Site. NB! TransactionMode is seen from Site perspective e.g. if a customer wants to 
 * buy Euro, TransactionMode should be SELL.
 */
public class Order implements Comparable<Order> {

	/**
	 * logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static {logger = Logger.getLogger("affix.java.project.moneyservice");}

	/**
	 * site a String holding the name of the Money Service site Order is intended for
	 */
	private final String site;

	/**
	 * currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 */
	private final String currencyCode;

	/**
	 * amount an int holding the amount of currency to order
	 */
	private final int amount;

	/**
	 * transactionMode a TransactionMode (Enum) defining if Site is going to sell or buy currency
	 */
	private final TransactionMode transactionMode;


	/**
	 * Default constructor for creating a complete Order object
	 * @param site a String holding the name of the Money Service site Order is intended for
	 * @param currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 * @param amount an int holding the amount of currency to order
	 * @param transactionMode a TransactionMode (Enum) defining if Site is going to sell or buy currency
	 * @throws IllegalArgumentException if parameters don't match requirements 
	 */
	public Order(String site, String currencyCode, int amount, TransactionMode transactionMode) {

		if(site.isEmpty()) {
			throw new IllegalArgumentException("Site name can NOT be empty!");
		}

		if((currencyCode.length() != 3 || !currencyCode.matches("^[A-Z]*$"))) {
			throw new IllegalArgumentException("Currency code has invalid format!");
		}

		if(amount <= 0) {
			throw new IllegalArgumentException("Amount can NOT be 0 or less than 0!");
		}

		this.site = site;
		this.currencyCode = currencyCode;
		this.amount = amount;
		this.transactionMode = transactionMode;			
	}


	/**
	 * Getter for attribute site
	 * @return site a String holding the name of the Money Service site Order is intended for
	 */
	public String getSite() {
		return site;
	}

	/**
	 * Getter for attribute currencyCode
	 * @return currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * Getter for attribute amount
	 * @return amount an int holding the amount of currency to order
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Getter for attribute transactionMode
	 * @return transactionMode a TransactionMode (Enum) defining if Site is going to sell or buy currency
	 */
	public TransactionMode getTransactionMode() {
		return transactionMode;
	}


	/**
	 * HashCode algorithm computed on all Order class attributes except the logger
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		logger.finest("Order hashCode used");

		final int prime = 31;
		int result = 13;

		result = prime * result + site.hashCode();
		result = prime * result + currencyCode.hashCode();
		result = prime * result + amount;
		result = prime * result + transactionMode.hashCode();

		return result;
	}

	/**
	 * Equals algorithm includes all Order class attributes except the logger
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		logger.finest("Order equals used");

		if(obj instanceof Order) {
			Order that = (Order) obj;
			if(this.site.equals(that.site)) {
				if(this.currencyCode.equals(that.currencyCode)) {
					if(this.amount == that.amount) {
						return this.transactionMode.equals(that.transactionMode);
					}
				}
			}	
		}
		
		return false;
	}

	/**
	 * Compares Order object by attribute site(alphabetic), currencyCode(alphabetic), 
	 * transactionMode(alphabetic) and at last amount(highest number first).
	 * @see Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Order that) {
		logger.finest("Order compareTo used");

		int compareVal = this.site.compareTo(that.site);
		if(compareVal == 0) {	// if same site then compare currencyCode
			compareVal = this.currencyCode.compareTo(that.currencyCode);

			if(compareVal == 0) { // if same currencyCode compare transactionMode
				compareVal = this.transactionMode.toString().compareTo(that.transactionMode.toString());

				if(compareVal == 0) { // if same transactionMode compare amount
					return -(this.amount - that.amount);
				}
			}	
		}

		return compareVal;
	}

	/**
	 * Converting object data to human readable format
	 * @return a String using format 
	 * {@code "Order [site=<site>, currencyCode=<currencyCode>, amount=<amount>, transactionMode=<transactionMode>]"}
	 * 
	 */
	@Override
	public String toString() {
		return String.format("Order [site=%s, currencyCode=%s, amount=%d, transactionMode=%s]"
				, site, currencyCode, amount, transactionMode);
	}
}
