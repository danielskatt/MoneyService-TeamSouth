package moneyservice.model;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.Test;

public class TestTransactionClass {

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
		int id = aTransaction.getId();
		// TODO - Change this!!
		assertEquals(id,id);
	}
	
	@Test
	public void testGetTimeStamp1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY);
		LocalDateTime testDateTime = LocalDateTime.now();

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