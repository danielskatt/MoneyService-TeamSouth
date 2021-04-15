package moneyservice.java.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TestSiteClass {
	
	
	@Test
	public void testSiteConstructor1() {
		Site south = new Site("South");
	}
	
	@Test
	public void testReadAmountOfCash() {
		Map<String, Integer> testMapForCash = new HashMap<>();
		
		Site south = new Site("South");
		Map<String, Integer> amountOfCash = south.readAmountOfCash(); 
		
		assertTrue(testMapForCash.equals(amountOfCash))
	}
	
	@Test
	public void testBuyMoney1() {
		Order od = new Order("South","RUB",31000,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertEquals(approved == false);
	}
	
	@Test
	public void testBuyMoney2() {
		Order od = new Order("South","RUB",31000,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertEquals(approved == true);
	}
	
	@Test
	public void testBuyMoney3() {
		Order od = new Order("South","",31000,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertEquals(approved == true);
	}
	
	@Test
	public void testsellMoney1() {
		Order od = new Order("South","USD",1000,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertEquals(approved == true);
	}
	
	@Test
	public void testsellMoney2() {
		Order od = new Order("South","AUD",1000,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertEquals(approved == true);
	}
	

	@Test
	public void testStoreTransaction() {
		List <Transaction> transactions = new ArrayList<>();
		Order od = new Order("South","RUB",1000,TransactionMode.BUY);
		
		Transaction aTransaction = new Transaction(od);
		
		transactions.add(aTransaction);
		
		assertFalse(transactions.isEmpty());
	}
	
	
	

}
