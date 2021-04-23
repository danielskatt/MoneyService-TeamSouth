package moneyservice.common;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Transaction implements java.io.Serializable {
		

	/**
	 * @attribute serialVersionUID holds the serial number for this class
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @attribute id - holds the id for this object
	 */
	private final int id;
	
	/**
	 * @attribute timeStamp - holds the time when this object was made
	 */
	private final LocalDateTime timeStamp;
	
	/**
	 * @attribute currencyCode - Holds information about which currecyCode (example USD) for the Transaction
	 */
	private final String currencyCode;
	
	/**
	 * @attribute amount - Holds information about the amount of currencyCode for the Transaction
	 */
	private final int amount;
	
	/**
	 * @attribute transactionMode - Holds information about if the type of Transaction, for example Sell or Buy
	 */
	private final TransactionMode mode;
	
	/**
	 * @attribute uniqueId - Holds information about the start id for the transaction
	 */
	private static int uniqueId = 1;
	
	
	
	/**
	 * Overloaded constructor
	 * @param orderData - Defines the order to convert to transaction
	 */
	public Transaction(Order orderData) {
		
		this.id = uniqueId++;
		this.timeStamp = LocalDateTime.of(Configuration.getCURRENT_DATE(), LocalTime.now());
		this.currencyCode = orderData.getCurrencyCode();
		this.amount = orderData.getAmount();
		this.mode = orderData.getTransactionMode();
	}
	/**
	 * 
	 * @return id - Returns the id for the Transaction object
	 */
	public int getId() {
		return id;
	}

	/**
	 * 
	 * @return timeStamp - Returns the time this transaction was made
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * 
	 * @return currencyCode - Returns the code for the currency, for example USD
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	
	/**
	 * 
	 * @return amount - Returns the amount for this transaction
	 */
	public int getAmount() {
		return amount;
	}

	
	/**
	 * 
	 * @return mode - Returns the transactionMode for this transaction, for example BUY or SELL
	 */
	public TransactionMode getMode() {
		return mode;
	}	
}
