package moneyservice.model;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import moneyservice.model.*;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestSiteClass {
	
	private Site south = new Site("South");
	
	/**
	 * Set up Site configuration
	 */
	@Test
	public void firstSetUpConfig() {
		Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
	}
	
	/**
	 * Test attributes of Site and correct configuraion
	 */
	@Test
	public void firstTestSiteConstructor() {
		assertNotNull(south);
	}
	@Test
	public void firstTestGetAvaliableAmount2() {
		String currencyCode = "EUR";
		Order od = new Order("South","EUR",2000,TransactionMode.BUY);
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		assertFalse(amount.isEmpty());
	}
	@Test
	public void firstTestGetAvaliableAmount1() {
		String currencyCode = "EUR";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		assertEquals(5000, theAmount);
	}
	@Test
	public void firstTestGetAvaliableAmount3() {
		String currencyCode = "WON";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		assertTrue(amount.isEmpty());
	}
	@Test
	public void firstTestGetAvaliableAmount4() {
		String currencyCode = "sek";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		assertTrue(amount.isEmpty());
	}
	@Test
	public void firstTestGetAvaliableAmount5() {
		String currencyCode = "SEK";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		assertEquals(50000, theAmount);
	}
	@Test
	public void firstTestGetAvaliableAmount6() {
		String currencyCode = "AUD";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		assertEquals(1500, theAmount);
	}
	@Test
	public void firstTestGetCurrencyMap() {		
		assertFalse(south.getCurrencyMap().isEmpty());
	}
	
	/**
	 * Test methods in Site
	 */
	@Test
	public void testSellMoney1() {
		String currencyCode = "RUB";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		Order od = new Order("South",currencyCode, theAmount/2, TransactionMode.BUY);
		boolean approved = south.sellMoney(od); 
		assertTrue(approved);
	}
	@Test
	public void testSellMoney2() {
		String currencyCode = "RUB";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		Order od = new Order("South",currencyCode,theAmount+1,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);
		assertFalse(approved);  
	}
	@Test
	public void testSellMoney3() {
		String currencyCode = "AUD";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		Order od = new Order("South",currencyCode,theAmount,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);  
		assertTrue(approved);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testSellMoney4() {
		Order od = new Order("South"," ",50,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);
		assertFalse(approved);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testSellMoney5() {
		Order od = new Order("South","SEKK",350,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);
		assertFalse(approved);
	}
	@Test
	public void testSellMoney6() {
		String currencyCode = "JPY";
		Optional<Double> amount = south.getAvailableAmount(currencyCode);
		double availableAmount = amount.get();
		int theAmount = (int)availableAmount;
		Order od = new Order("South",currencyCode,theAmount,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);  
		assertTrue(approved);
	}
	@Test
	public void testSellMoney7() {
		String currencyCode = "NOK";
		Order od = new Order("North",currencyCode, 2,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);  
		assertFalse(approved);
	}
	@Test
	public void testSellMoney8() {
		String currencyCode = "NOK";
		Order od = new Order("South",currencyCode, 0,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);  
		assertFalse(approved);
	}
	@Test
	public void testBuyMoney1() {
		String currencyCode = "USD";
		Order od = new Order("South", currencyCode, 100, TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertTrue(approved);
	}
	@Test
	public void testBuyMoney2() {
		String currencyCode = "AUD";
		Order od = new Order("South",currencyCode,1000,TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertTrue(approved);
	}
	@Test
	public void testBuyMoney3() {
		String currencyCode = "CHF";
		Order od = new Order("South",currencyCode, 300,TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertTrue(approved);
	}
	@Test(expected = IllegalArgumentException.class)
	public void testBuyMoney4() {
		String currencyCode = "VIX";
		Order od = new Order("South",currencyCode,5500,TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertFalse(approved);
	}
	@Test
	public void testBuyMoney5() {
		String currencyCode = "AUD";
		Order od = new Order("South",currencyCode,300000,TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertFalse(approved);
	}
	@Test
	public void testBuyMoney6() {
		String currencyCode = "CHF";
		Order od = new Order("North",currencyCode,3,TransactionMode.SELL);
		boolean approved = south.buyMoney(od);
		assertFalse(approved);
	}
	@Test
	public void testBuyMoney7() {
		String currencyCode = "NOK";
		Order od = new Order("North",currencyCode,0,TransactionMode.BUY);
		boolean approved = south.buyMoney(od);  
		assertFalse(approved);
	}
	@Test
	public void testStoreTransaction1() {
		String site = "SOUTH";
		String directory = ".." + File.separator + "HQ" + File.separator;
		File path = new File(directory+site);
		boolean folderCreated = path.mkdir();
		File file = new File(directory + site + File.separator + "Report_" + site + "_" + Configuration.getCURRENT_DATE().toString() + ".ser");
		
		Order od = new Order("South","RUB",10,TransactionMode.BUY);
		boolean approved = south.sellMoney(od);
		south.shutDownService(file.toString());
		List<Transaction> transactions = MoneyServiceIO.readReportAsSer(file.toString());
		
		assertTrue(approved);
		assertEquals(1, transactions.size());
	}
}
