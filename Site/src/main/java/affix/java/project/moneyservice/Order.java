package affix.java.project.moneyservice;

import java.util.logging.Logger;

/** -------------------- Order ----------------------
 * <p>
 *  Holds information about an Order. 
 *  Can only be created by creating an Order object 
 *  with all necessary information available.
 * <p>
 * --------------------------------------------------*/
public class Order implements Comparable<Order> {
	/**
	 * @attribute site - Holds information about which Site the order is intended for
	 */
	private final String site;
	/**
	 * @attribute currencyCode - Holds information about which currencyCode (example USD) for the Order 
	 */
	private final String currencyCode;
	/**
	 * @attribute amount - Holds information about the amount of currencyCode for the Order 
	 */
	private final int amount;
	/**
	 * @attribute transactionMode - Holds information about if the type of Order, for example Sell or Buy
	 */
	private final TransactionMode transactionMode;
	
	private static Logger logger;
	
	static{
		logger = Logger.getLogger("affix.java.project.moneyservice");
	}
	
	/**
	 * Overloaded constructor
	 * @param site - Defines the specific Site the Order is intended for
	 * @param currencyCode - Defines the code for the currency
	 * @param amount - Defines the amount of the currencyCode for the order
	 * @param transactionMode - Defines the type of Order
	 */
	public Order(String site, String currencyCode, int amount, TransactionMode transactionMode) {
		if(amount <= 0) {
			throw new IllegalArgumentException("Amount cannot be 0 or lower!");
		}
		this.site = site;
		this.currencyCode = currencyCode;
		this.amount = amount;
		this.transactionMode = transactionMode;
	}
	
	/**
	 * 
	 * @return site
	 */
	public String getSite() {
		return site;
	}

	/**
	 * 
	 * @return currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * 
	 * @return amount
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * 
	 * @return transactionMode
	 */
	public TransactionMode getTransactionMode() {
		return transactionMode;
	}

	/**
	 * toString - returns a String with information about an Order
	 */
	@Override
	public String toString() {
		return String.format("Order: %s %d of %s to %s", transactionMode, amount, currencyCode, site);
	}

	/*
	 * @see Object - hashCode()  
	 */
	@Override
	public int hashCode() {
		logger.finest("Order hashCode used");
		final int prime = 31;
		int result = 1;
		result = prime * result + amount;
		result = prime * result + ((currencyCode == null) ? 0 : currencyCode.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result + ((transactionMode == null) ? 0 : transactionMode.hashCode());
		return result;
	}

	/*
	 * @see Object - equals(object)
	 */
	@Override
	public boolean equals(Object obj) {
		logger.finest("Order equals used");
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (amount != other.amount)
			return false;
		if (currencyCode == null) {
			if (other.currencyCode != null)
				return false;
		} else if (!currencyCode.equals(other.currencyCode))
			return false;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		if (transactionMode != other.transactionMode)
			return false;
		return true;
	}

	/*
	 * 
	 */
	@Override
	public int compareTo(Order that) {
		logger.finest("Order compareTo used");
		if(this.amount == that.amount) {
			if(this.currencyCode.equals(that.currencyCode)) {
				if(this.transactionMode == that.transactionMode) {
					return (this.site.compareTo(that.site));
				}
				else {
					return (this.transactionMode.compareTo(that.transactionMode));
				}
			}
			else {
				return (this.currencyCode.compareTo(that.currencyCode));
			}
		}
		return (this.amount - that.amount);
	}

}
