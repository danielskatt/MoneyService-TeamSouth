package affix.java.project.moneyservice;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestTransactionClass {
	
	/**
	 * Set up Site configuration
	 */
	@Test
	public void firstSetUpConfig() {
		Configuration.parseConfigFile("TestConfigFiles/TestConfig_2021-04-01.txt");
		assertNotNull(Configuration.getBoxOfCash());
	}

	@Test
	public void testCreateTransaction1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);
		assertNotNull(aTransaction);
	}
	
	@Test
	public void testGetId1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);
		assertTrue(aTransaction.getId()>0);
	}
	
	@Test
	public void testGetTimeStamp1() {
		LocalDateTime testDateTime = LocalDateTime.of(Configuration.getCURRENT_DATE(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond()));
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);

		assertTrue(testDateTime.isBefore(aTransaction.getTimeStamp()) ||
				testDateTime.isEqual(aTransaction.getTimeStamp()));
	}

	@Test
	public void testGetCurrencyCode1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);
		String currencyCode = aTransaction.getCurrencyCode();
		
		assertTrue(currencyCode.equals("USD"));
	}
	
	@Test
	public void testGetAmount1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);
		int amount = aTransaction.getAmount();
		
		assertEquals(amount, 100);
	}
	
	@Test  
	public void testGetMode1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		Transaction aTransaction = new Transaction(od);  
		TransactionMode mode = aTransaction.getMode();
		
		assertEquals(mode, TransactionMode.BUY);
	}

}
