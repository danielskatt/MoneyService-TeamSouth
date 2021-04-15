package moneyservice'.java.app;

import java.time.LocalDateTime;

public class Transaction {
		
	// ID of this transaction
	private int id;
	
	// Time when the trade was made 
	private LocalDateTime timeStamp;
	
	// Code for the currency
	private String currencyCode;
	
	// The amount traded of this transaction
	private int amount;
	
	// If the trade are of sell or buy
	private TransactionMode mode;
	
	static int uniqueId = 1;
	
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
