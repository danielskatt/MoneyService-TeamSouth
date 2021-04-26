package moneyservice.model;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;

import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.TransactionMode;

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
		assertNotNull(od);
	}
	
	@Test
	public void testGetSite1() {
		String site = "South";
		Order od = new Order(site,"USD",100, TransactionMode.BUY);
		String orderSiteName = od.getSite();
		assertEquals(site, orderSiteName);
	}
	
	@Test
	public void testGetCurrencyCode1() {
		String currencyCode = "USD";
		Order od = new Order("South",currencyCode,100, TransactionMode.BUY);
		String orderCurrencyCode = od.getCurrencyCode();
		assertEquals(currencyCode, orderCurrencyCode);
	}
	
	@Test
	public void testGetAmount1() {
		int amount = 100;
		Order od = new Order("South","USD",amount, TransactionMode.BUY);
		int orderAmount = od.getAmount();
		assertEquals(amount, orderAmount);
	}
	
	@Test
	public void testGetMode() {
		TransactionMode mode = TransactionMode.BUY;
		Order od = new Order("South","USD",100, mode);
		TransactionMode aMode = od.getTransactionMode();
		assertEquals(mode, aMode);
	}
	
	@Test
	public void testHashCode1() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertEquals(o1.hashCode(), o2.hashCode());
	}
	@Test
	public void testHashCode2() {
		Order o1 = new Order("North","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}
	@Test
	public void testHashCode3() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","SEK",100, TransactionMode.BUY);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}
	@Test
	public void testHashCode4() {
		Order o1 = new Order("South","USD",10, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}
	@Test
	public void testHashCode5() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.SELL);
		assertNotEquals(o1.hashCode(), o2.hashCode());
	}
	@Test
	public void testEquals1() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertTrue(o1.equals(o2));
	}
	@Test
	public void testEquals2() {
		Order o1 = new Order("East","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertFalse(o1.equals(o2));
	}
	@Test
	public void testEquals3() {
		Order o1 = new Order("South","SEK",100, TransactionMode.BUY);
		Order o2 = new Order("South","NOK",100, TransactionMode.BUY);
		assertFalse(o1.equals(o2));
	}
	@Test
	public void testEquals4() {
		Order o1 = new Order("South","SEK",102, TransactionMode.BUY);
		Order o2 = new Order("South","SEK",101, TransactionMode.BUY);
		assertFalse(o1.equals(o2));
	}
	@Test
	public void testEquals5() {
		Order o1 = new Order("South","SEK",100, TransactionMode.BUY);
		Order o2 = new Order("South","SEK",100, TransactionMode.SELL);
		assertFalse(o1.equals(o2));
	}
	@Test
	public void compareTo1() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertEquals(0, o1.compareTo(o2));
	}
	
}
