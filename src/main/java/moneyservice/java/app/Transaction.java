package moneyservice'.java.app;

import java.time.LocalDateTime;

public class Transaction implements java.io.Serializable {
		

	// Serial ID
	private static final long serialVersionUID = 1L;

	// ID of this transaction
	private final int id;
	
	// Time when the trade was made 
	private final LocalDateTime timeStamp;
	
	// Code for the currency
	private final String currencyCode;
	
	// The amount traded of this transaction
	private final int amount;
	
	// If the trade are of sell or buy
	private final TransactionMode mode;
	
	
	private static final int uniqueId = 1;
	
	// Constructor
	public Transaction(Order orderData) {
		
		this.id = uniqueId++;
		this.timeStamp = LocalDateTime.now();
		this.currencyCode = orderData.getCurrencyCode();
		this.amount = orderData.getAmount();
		this.mode = orderData.getMode();
	}

	// will these be necessary?
	public int getId() {
		return id;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public int getAmount() {
		return amount;
	}

	public TransactionMode getMode() {
		return mode;
	}	
}
