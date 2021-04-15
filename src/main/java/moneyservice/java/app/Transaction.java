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
	
	public Transaction(int amount, String currencyCode, TransactionMode mode) {
		this.id = uniqueId++;
		this.timeStamp = LocalDateTime.now();
		this.currencyCode = currencyCode;
		this.amount = amount;
		this.mode = mode;
	}


	// will these be necessary?
	public int getId() {
		return id;
	}


	public int getAmount() {
		return amount;
	}


	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}
	
}
