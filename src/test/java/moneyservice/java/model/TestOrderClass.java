package moneyservice.java.model;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

public class TestOrderClass {

	@Test
	public void testOrderConstructor1() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		assertNotNull(od);
	}
	
	@Test
	public void testOrderConstructor2() {
		Order od = new Order("South","APP",100, TransactionMode.BUY);
		assertNotNull(od);
	}
	
	@Test
	public void testOrderConstructor3() {
		Order od = new Order("South","WON",100, TransactionMode.SELL);
		assertNull(od);
	}
	
	@Test
	public void testGetSite1() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		String siteName = od.getSite();
		assertTrue(siteName.equals("South"));
	}
	
	@Test
	public void testGetCurrencyCode1() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		String currencyCode = od.getCurrencyCode();
		assertTrue(currencyCode.equals("USD"));
	}
	
	@Test
	public void testGetAmount1() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		int amount = od.getAmount();
		assertEquals(amount, 100);
	}
	
	@Test
	public void testGetMode() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		TransactionMode aMode = od.getMode();
		assertTrue(aMode, TransactionMode.BUY);
	}

}
