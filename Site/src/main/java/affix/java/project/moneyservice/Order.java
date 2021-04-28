package affix.java.project.moneyservice;

import java.util.logging.Logger;

/** 
 * This class defines a Order in MoneyService
 * Order should be created by specifying site name, currency code, the amount and 
 * transaction mode, buy or sell. 
 * NB! TransactionMode is seen from customer perspective e.g. if a customer wants to 
 * buy Euro, TransactionMode should be BUY.
 */

public class Order implements Comparable<Order> {
	
	/**
	 * @attribute logger a Logger
	 */
	private static Logger logger;
	/**
	 * Setter for attribute logger
	 */
	static {logger = Logger.getLogger("affix.java.project.moneyservice");}
	
	/**
	 * @attribute site a String holding the name of the Money Service site Order is intended for
	 */
	private final String site;
	
	// capital and 3 letters
	/**
	 * @attribute currencyCode a String defining the code of the currency e.g. USD or EUR to order
	 */
	private final String currencyCode;
	
	/**
	 * @attribute amount an int holding the amount of currency to order
	 */
	private final int amount;
	
	/**
	 * @attribute transactionMode a TransactionMode (Enum) defining if customer want to sell or buy currency
	 */
	private final TransactionMode transactionMode;
	
	
	/**
	 * Default constructor for creating a complete Order object
	 * @param site a String holding the name of the Money Service site Order is intended for
	 * @param currencyCode a String defining the code of the currency e.g. USD or EUR to order
	 * @param amount an int holding the amount of currency to order
	 * @param transactionMode a TransactionMode (Enum) defining if customer want to sell or buy currency
	 * @throws IllegalArgumentException if parameters don't match requirements 
	 */
	public Order(String site, String currencyCode, int amount, TransactionMode transactionMode) {
		try {
			if(site.isEmpty() || site == null) {
				throw new IllegalArgumentException("Site name can NOT be empty!");
			}
			
			if((currencyCode.length() != 3 || !currencyCode.matches("^[A-Z]*$"))) {
				throw new IllegalArgumentException("Site name can NOT be empty!");
			}
			
			if(amount <= 0) {
				throw new IllegalArgumentException("Amount can NOT be 0 or less than 0!");
			}
			
		}
		catch(NullPointerException e) {
			
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
	 * @return currencyCode a String defining the code of the currency e.g. USD or EUR to order
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
	 * @return transactionMode a TransactionMode (Enum) defining if customer want to sell or buy currency
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
		
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + amount;
//		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
//		result = prime * result + ((site == null) ? 0 : site.hashCode());
//		result = prime * result + ((transactionMode == null) ? 0 : transactionMode.hashCode());
//		return result;
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
		
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		Order other = (Order) obj;
//		if (amount != other.amount)
//			return false;
//		if (currencyCode == null) {
//			if (other.currencyCode != null)
//				return false;
//		} else if (!currencyCode.equals(other.currencyCode))
//			return false;
//		if (site == null) {
//			if (other.site != null)
//				return false;
//		} else if (!site.equals(other.site))
//			return false;
//		if (transactionMode != other.transactionMode)
//			return false;
//		return true;
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
	 * {"Order [site=<site>, currencyCode=<currencyCode>, amount=<amount>, transactionMode=<transactionMode>]"}
	 */
	@Override
	public String toString() {
		return String.format("Order [site=%s, currencyCode=%s, amount=%d, transactionMode=%s]"
				, site, currencyCode, amount, transactionMode);
	}
}
