package moneyservice.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moneyservice.model.*;

import org.junit.Ignore;
import org.junit.Test;

public class TestSiteClass {
	
	
	@Test
	public void testSiteConstructor1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		
		assertNotNull(south);
	}
	
	@Test
	public void testBuyMoney1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","RUB",10000,TransactionMode.BUY);
		Site south = new Site("South");
		boolean approved = south.buyMoney(od); 
		assertTrue(approved);
	}
	 
	@Test
	public void testBuyMoney2() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","RUB",31000,TransactionMode.BUY);
		Site south = new Site("South");
		boolean approved = south.buyMoney(od);
		assertFalse(approved);  
	}
	
	@Test
	public void testBuyMoney3() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","AUD",50,TransactionMode.BUY);
		Site south = new Site("South");
		boolean approved = south.buyMoney(od);  
		assertTrue(approved);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuyMoney4() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South"," ",50,TransactionMode.BUY);
		Site south = new Site("South");
		boolean approved = south.buyMoney(od);
		assertFalse(approved);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testBuyMoney5() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","RUL",350,TransactionMode.BUY);
		Site south = new Site("South");
		boolean approved = south.buyMoney(od);
		assertFalse(approved);
	}
	
	@Test
	public void testsellMoney1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","USD",1000,TransactionMode.SELL);
		Site south = new Site("South");
		boolean approved = south.sellMoney(od);
		assertTrue(approved);
	}
	
	@Test
	public void testsellMoney2() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Order od = new Order("South","AUD",1000,TransactionMode.SELL);
		Site south = new Site("South");
		boolean approved = south.sellMoney(od);
		assertTrue(approved);
	}
	
	@Test
	public void testSellMoney3() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		Order od = new Order("South","CHF",5500,TransactionMode.SELL);
		boolean approved = south.sellMoney(od);
		assertFalse(approved);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testSellMoney4() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		Order od = new Order("South","VIX",5500,TransactionMode.SELL);
		boolean approved = south.sellMoney(od);
		assertFalse(approved);
	}
	
	@Ignore
	@Test
	public void testShutDownService1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		String serializableFile = "test1.ser";
		south.printSiteReport(serializableFile);
	}

	@Test
	public void testGetCurrencyMap1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		//Map<String, Currency> testMapForCurrency = south.getCurrencyMap();
		
		//assertFalse(testMapForCurrency.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount1() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		String currencyCode = "EUR";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		
		assertTrue(amount.get()>0);
	}
	
	@Test
	public void testGetAvaliableAmount2() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		String currencyCode = "EUR";
		Order od = new Order("South","EUR",2000,TransactionMode.BUY);
		
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		
		assertFalse(amount.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount3() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		String currencyCode = "WON";
		
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		
		assertTrue(amount.isEmpty());
	}
	
	@Test
	public void testGetAvaliableAmount4() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		Site south = new Site("South");
		String currencyCode = "eur";
		
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		
		assertTrue(amount.isEmpty());
	}
	
	@Test
	public void testStoreTransaction1() {
		List <Transaction> transactions = new ArrayList<>();
		Order od = new Order("South","RUB",1000,TransactionMode.BUY);
		
		Transaction aTransaction = new Transaction(od);
		
		transactions.add(aTransaction);
		
		assertFalse(transactions.isEmpty());
	}
	
	
	

}
