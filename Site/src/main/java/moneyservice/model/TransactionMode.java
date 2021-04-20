package moneyservice.model;

/* An enum holding values SELL and BUY
 * seen from the User's perspective.
 * Used in Order class as a parameter, set to determine
 * if an order is sell (Buy from the Site's perspective)
 * and buy (Sell from the Site's perspective). 
 */

public enum TransactionMode {
	SELL, 
	BUY,
}
