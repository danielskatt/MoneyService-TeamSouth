package affix.java.project.moneyservice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

public class TestOrderClass {

	@Test
	public void testOrderCtorNormal() {
		Order od = new Order("South","USD",100, TransactionMode.BUY);
		assertNotNull(od);
	}
	
	@Test
	public void testOrderCtorSite1() {
		Order od = new Order("thisIsNotASiteName", "EUR", 100, TransactionMode.SELL);

		assertNotNull(od);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testOrderCtorSite2() {
		
		@SuppressWarnings("unused")
		Order od = new Order("", "EUR", 100, TransactionMode.SELL);
	}
	
	@Ignore
	@Test
	public void testOrderCtorSite3() {
		String site = null;
		Order od = new Order(site, "EUR", 100, TransactionMode.SELL);
		
		assertTrue(od.getSite() == null);
	}
	
	@Test
	public void testOrderCtorCurrency1() {
		Order od = new Order("South","APP",100, TransactionMode.BUY);
		assertNotNull(od);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testOrderCtorCurrency2() {
		Order od = new Order("South", "thisIsNotACurrencyCode", 100, TransactionMode.SELL);

		assertNotNull(od);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testOrderCtorAmountZero() {
		
		@SuppressWarnings("unused")
		Order od = new Order("South","EUR", 0, TransactionMode.SELL);

	}
	@Test (expected = IllegalArgumentException.class)
	public void testOrderCtorAmountNegative() {
		
		@SuppressWarnings("unused")
		Order od = new Order("South","EUR", -10, TransactionMode.SELL);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testOrderCtor7() {
		
		@SuppressWarnings("unused")
		Order od = new Order("South","EUR", 100, TransactionMode.valueOf("HEJ"));
	}
	
	
	@Test
	public void testOrderGetSite1() {
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
	public void testHashCode6() {
		Order o1 = new Order("SOUTH","USD",10, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		
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
	public void testEquals6() {
		Order o1 = new Order("SOUTH","SEK",100, TransactionMode.BUY);
		Order o2 = new Order("South","SEK",100, TransactionMode.SELL);
		
		assertFalse(o1.equals(o2));
	}
	
	@Test
	public void compareTo1Equals() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		assertEquals(0, o1.compareTo(o2));
	}
	
	@Test
	public void compareTo2Site() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("North","USD",100, TransactionMode.BUY);
		
		assertTrue(o1.compareTo(o2) > 0);
	}
	
	@Test
	public void compareTo3Currency() {
		Order o1 = new Order("South","USD",100, TransactionMode.BUY);
		Order o2 = new Order("South","AUD",100, TransactionMode.BUY);

		assertTrue(o1.compareTo(o2) > 0);
	}
	
	@Test
	public void compareTo4TransactionMode() {
		Order o1 = new Order("South","USD",100, TransactionMode.SELL);
		Order o2 = new Order("South","USD",100, TransactionMode.BUY);
		
		assertTrue(o1.compareTo(o2) > 0);
	}
	
	@Test
	public void compareTo5Amount() {
		Order o1 = new Order("South", "USD", 100, TransactionMode.SELL);
		Order o2 = new Order("South", "USD", 5000, TransactionMode.SELL);
		
		assertTrue(o1.compareTo(o2) > 0);
	}
	
	@Ignore
	@Test
	public void compareTo6() {
		List<Order> oList = new ArrayList<>();
		oList.add(new Order("South", "USD", 100, TransactionMode.SELL));
		oList.add(new Order("North", "USD", 100, TransactionMode.SELL));
		oList.add(new Order("South", "AUD", 100, TransactionMode.BUY));
		oList.add(new Order("South", "AUD", 100, TransactionMode.SELL));
		oList.add(new Order("South", "AUD", 5000, TransactionMode.BUY));
		
		Collections.sort(oList);
		
		for(Order o: oList) {
			System.out.println(o);
		}
		
		assertTrue(true);
	}
	
	
	@Test
	public void testOrderToString() {
		Order o = new Order("South", "EUR", 100, TransactionMode.SELL);
		String expected = "Order [site=South, currencyCode=EUR, amount=100, transactionMode=SELL]";
		
		assertEquals(expected, o.toString());
	}
	
	
}
