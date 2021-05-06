package affix.java.project.moneyservice;

/**
 * An enumeration holding values SELL and BUY seen from the Money Service Site's perspective.
 * Used in Order and Transaction class as a parameter to determine if the order from 
 * a customer is of type BUY or SELL.
 * TransactionMode is seen from the perspective Money Service Site's. 
 */
public enum TransactionMode {
	
	/**
	 * SELL an enumeration value defining that Money Service Site is selling currency to a customer
	 */
	SELL,
	
	/**
	 * BUY an enumeration value defining that Money Service Site is buying currency from a customer
	 */
	BUY,
}
