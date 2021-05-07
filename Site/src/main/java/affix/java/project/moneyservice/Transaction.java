package affix.java.project.moneyservice;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**                                
 * This class represents a transaction in the MoneyService system. 
 * A transaction is described by Currency, amount, and buy/sell mode. 
 * A time stamp is needed to recreate the Order based on stored rates 
 * Bookkeeping requires that all transaction also holds a unique id. 
 * It is only used internally so default serialization will do  
 */
public class Transaction implements java.io.Serializable {

	/**
	 * serialVersionUID holds the serial number for this class
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * id an int holding a unique id
	 */
	private final int id;

	/**
	 * timeStamp a LocalDateTime holding the time when this object was made
	 */
	private final LocalDateTime timeStamp;

	/**
	 * currencyCode a String defining the code of the currency in three capital letters e.g. USD or EUR
	 */
	private final String currencyCode;

	/**
	 * amount an int holding information about the amount of the currency (currencyCode)
	 */
	private final int amount;

	/**
	 * mode a Transaction defining if the Transaction is of type BUY or SELL 
	 */
	private final TransactionMode mode;

	/**
	 * uniqueId defining the unique id for a Transaction object
	 */
	private static int uniqueId = 1;


	/**
	 * Default constructor for creating a complete Transaction by converting a Order object
	 * @param orderData an Order holding all data for creating a complete Transaction object
	 */
	public Transaction(Order orderData) {

		this.id = uniqueId++;
		this.timeStamp = LocalDateTime.of(Configuration.getCURRENT_DATE(), LocalTime.now());
		this.currencyCode = orderData.getCurrencyCode();
		this.amount = orderData.getAmount();
		this.mode = orderData.getTransactionMode();
	}
	
	/**
	 * Getter for attribute id
	 * @return id an int holding a unique id
	 */
	public int getId() {
		return id;
	}

	/**
	 * Getter for attribute timeStamp
	 * @return timeStamp a LocalDateTime holding the time when this object was made
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;
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
	 * @return amount an int holding information about the amount of the currency (currencyCode)
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * Getter for attribute mode
	 * @return mode a Transaction defining if the Transaction is of type BUY or SELL 
	 */
	public TransactionMode getMode() {
		return mode;
	}	

	@Override
	public String toString() {
		return "Transaction [id=" + id + ", timeStamp=" + timeStamp + ", currencyCode=" + currencyCode + ", amount="
				+ amount + ", mode=" + mode + "]";
	}
}
