package moneyservice.java.model;

/** -------------------- Order ----------------------
 * <p>
 *  Holds information about an Order. 
 *  Can only be created by creating an Order object 
 *  with all necessary information available.
 * <p>
 * --------------------------------------------------*/
public class Order {
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
	
	/**
	 * Overloaded constructor
	 * @param site - Defines the specific Site the Order is intended for
	 * @param currencyCode - Defines the code for the currency
	 * @param amount - Defines the amount of the currencyCode for the order
	 * @param transactionMode - Defines the type of Order
	 */
	public Order(String site, String currencyCode, int amount, TransactionMode transactionMode) {
		this.site = site;
		this.currencyCode = currencyCode;
		this.amount = amount;
		this.transactionMode = transactionMode;
	}

	// TODO: create methods for hashCode, compareTo and equals?
	
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

}
