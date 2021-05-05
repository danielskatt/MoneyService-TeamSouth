package affix.java.project.moneyservice;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Transaction;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestMoneyServiceIOCLass {
	
	private String filename = "TestConfigFiles/Report_TEST.ser";
	private String badFilename = "test";
	private String textFilename = "test.txt";

	/**
	 * Normal case
	 */
	@Test
	public void test1StoreTransactionAsSer() {
		List<Transaction> transactionList = new ArrayList<>();
		boolean stored = MoneyServiceIO.storeTransactionsAsSer(filename, transactionList);
		assertTrue(stored);
	}
	@Test
	public void test2ReadReportAsSer() {
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer(filename);
		assertNotNull(transactions);
		assertTrue(transactions.isEmpty());
	}
	
	/**
	 * Edge case
	 */
	@Test
	public void test3StoreTransactionAsSer() {
		List<Transaction> transactionList = null;
		boolean stored = MoneyServiceIO.storeTransactionsAsSer(filename, transactionList);
		assertTrue(stored);
	}
	
	/**
	 * Bad filename with no extension
	 */
	@Test
	public void test4StoreTransactionAsSer() {
		List<Transaction> transactionList = new ArrayList<>();
		boolean stored = MoneyServiceIO.storeTransactionsAsSer(badFilename, transactionList);
		assertFalse(stored);
	}
	@Test
	public void test5ReadReportAsSer() {
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer(badFilename);
		assertTrue(transactions.isEmpty());
	}
	
	/**
	 * Try to store the file as text
	 */
	@Test
	public void test6StoreTransactionAsSer() {
		List<Transaction> transactionList = new ArrayList<>();
		boolean stored = MoneyServiceIO.storeTransactionsAsSer(textFilename, transactionList);
		assertFalse(stored);
	}
	@Test
	public void test7ReadReportAsSer() {
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer(textFilename);
		assertTrue(transactions.isEmpty());
	}
	
	/**
	 * Try to read a file that does not exist
	 */
	@Test
	public void test8ReadReportAsSer() {
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer("shouldNotFind");
		assertTrue(transactions.isEmpty());
	}
	
	@Test
	public void test9StoreTransactionAsSerException() {
		List<Transaction> transactionList = new ArrayList<>();
		boolean stored = MoneyServiceIO.storeTransactionsAsSer(badFilename+"/\b.ser", transactionList);
		assertFalse(stored);
	}

	@Test
	public void test10StoreBoxOfCashAsTextException() {
		Map<String, Double> test = new HashMap<String,Double>();
		Double amount = 350.5;
		test.putIfAbsent("USD", amount);
		test.putIfAbsent("AUD", amount);
		
		boolean stored = MoneyServiceIO.storeBoxOfCashAsText(badFilename+"/\b.txt", test);
		assertFalse(stored);
	}
	
	@Test
	public void test11ReadReportAsSer() {
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer(badFilename+"/\b.ser");
		assertTrue(transactions.isEmpty());
	}
}
