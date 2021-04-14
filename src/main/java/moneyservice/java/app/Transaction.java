package project.java.moneyservice;

import java.time.LocalDateTime;

public class Transaction {
	
	static int uniqueId = 1;
	int id;
	int amount;
	TransactionMode mode;
	
	LocalDateTime timeStamp;
	
	
	public Transaction(int amount,TransactionMode mode) {
		this.amount = amount;
		this.mode = mode;
		this.timeStamp = LocalDateTime.now();
		this.id = uniqueId++;
	}


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
