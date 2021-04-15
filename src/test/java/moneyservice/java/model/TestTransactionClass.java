package moneyservice.java.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestTransactionClass {

	@Test
	public void testCreateTransaction1() {
		Order od = new Order("South","USD",100,TransactionMode.BUY)
		Transaction aTransaction = new Transaction(od)
		assertNotNull(aTransaction);
	}
	
	

}
