package moneyservice.site.library;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.Order;

public interface MoneyService {
	
	/**
	 * This method is used for a buying order 
	 * @param orderData holding value, currencyCode and transaction mode
	 * @return boolean holding outcome of operation
	 * @throws IllegalArgumentException if required currency is not accepted
	 */
	public boolean buyMoney(Order orderData) throws IllegalArgumentException;
	
	/**
	 * This method is used for a selling order 
	 * @param orderData holding value, currencyCode and transaction mode 
	 * @return boolean holding outcome of operation
	 * @throws IllegalArgumentException if required currency is not accepted
	 */
	public boolean sellMoney(Order orderData) throws IllegalArgumentException;
	
	/**
	 * This method is used to print a report presenting current currencies and their amount
	 * for an implementation of the MoneyService interface, i.e. a Site
	 * @param destination a String defining where to write the report, i.e. Console/Textfile
	 */
	public void printSiteReport(String destination);
	
	/**
	 * This method shuts down the service properly, i.e. closing any server/db connection.
	 * Storing data for all completed orders for future recovery etc
	 * @param destination a String defining file/db for data
	 */
	public void shutDownService(String destination);
	
	/**
	 * This method will export accepted currencies and their buy and sell rates 
	 * for the current session as configured at start up
	 * @return Map holding supported currencies using currency code as key
	 */
	default Map<String, Currency> getCurrencyMap() { return Collections.emptyMap(); }
			
	/**
	 * This method exports the current amount at an implementing site for a specified currency 
	 * @param currencyCode String holding currency code used in local site
	 * @return {@code Optional<Double>} holding current amount for required currency at site i/a
	 */
	default Optional<Double> getAvailableAmount(String currencyCode) { return Optional.empty(); }
}
