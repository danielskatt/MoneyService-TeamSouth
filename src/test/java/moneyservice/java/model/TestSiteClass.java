package moneyservice.java.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

public class TestSiteClass {
	
	
	@Test
	public void testSiteConstructor1() {
		Site south = new Site("South");
		assertNotNull(south);
	}
	
	@Test
	public void testGetCurrencyMap1() {
		Site south = new Site("South");
		Map<String, Currency> testMapForCurrency = south.getCurrencyMap(); 
		
		assertFalse(testMapForCurrency.isEmpty());
	}
	
	@Test
	public void testBuyMoney1() {
		Order od = new Order("South","RUB",10000,TransactionMode.BUY);
		boolean approved = buyMoney(od); 
		assertTrue(approved);
	}
	 
	@Test
	public void testBuyMoney2() {
		Order od = new Order("South","RUB",31000,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertFalse(approved);
	}
	
	@Test
	public void testBuyMoney3() {
		Order od = new Order("South","AUD",50,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertTrue(approved);
	}
	
	@Test
	public void testBuyMoney4() {
		Order od = new Order("South"," ",50,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertFalse(approved);
	}
	
	@Test
	public void testBuyMoney5() {
		Order od = new Order("South","RUL",350,TransactionMode.BUY);
		boolean approved = buyMoney(od);
		assertFalse(approved);
	}
	
	@Test
	public void testsellMoney1() {
		Order od = new Order("South","USD",1000,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertTrue(approved);
	}
	
	@Test
	public void testsellMoney2() {
		Order od = new Order("South","AUD",1000,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertTrue(approved);
	}
	
	@Test
	public void testSellMoney3() {
		Order od = new Order("South","CHF",5500,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertFalse(approved);
	}
	
	@Test
	public void testSellMoney3() {
		Order od = new Order("South","VIX",5500,TransactionMode.SELL);
		boolean approved = sellMoney(od);
		assertFalse(approved);
	}

	@Test
	public void testStoreTransaction() {
		List <Transaction> transactions = new ArrayList<>();
		Order od = new Order("South","RUB",1000,TransactionMode.BUY);
		
		Transaction aTransaction = new Transaction(od);
		
		transactions.add(aTransaction);
		
		assertFalse(transactions.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount1() {
		Site south = new Site("South");
		String currencyCode = "EUR";
		Optional<Double> amount = south.getAvaliableAmount(currencyCode);
		
		assertTrue(amount.get()>0);
	}
	
	@Test
	public void testGetAvaliableAmount2() {
		Site south = new Site("South");
		String currencyCode = "EUR";
		Order od = new Order("South","EUR",2000,TransactionMode.BUY);
		
		Optional<Double> amount = south.getAvaliableAmount(currencyCode);
		
		assertTrue(amount.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount3() {
		Site south = new Site("South");
		String currencyCode = "WON";
		
		Optional<Double> amount = south.getAvaliableAmount(currencyCode);
		
		assertTrue(amount.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount4() {
		Site south = new Site("South");
		String currencyCode = "eur";
		
		Optional<Double> amount = south.getAvaliableAmount(currencyCode);
		
		assertTrue(amount.isEmpty());
	}
	
	
	

}
