package affix.java.project.moneyservice;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCurrencyClass {

	@Test
	public void testCurrencyConstructor1() {
		String code = "USD";
		float rate = 9.23F;
		Currency testCurrency = new Currency(code,rate);
		
		assertNotNull(testCurrency);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testCurrencyConstructor2() {
		@SuppressWarnings("unused")
		Currency currency = new Currency("test", 1.03F);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testCurrencyConstructor3() {
		@SuppressWarnings("unused")
		Currency currency = new Currency("TEST", 1.03F);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testCurrencyConstructor4() {
		@SuppressWarnings("unused")
		Currency currency = new Currency("usd", 1.03F);
	}
	
	@Test
	public void testGetCurrencyCode1() {
		Currency testCurrency = new Currency("USD",9.22F);
		String theCurrencyCode = testCurrency.getCurrencyCode();
		
		assertTrue(theCurrencyCode.equals("USD"));
	}
	
	@Test
	public void testGetRate1() {
		Currency testCurrency = new Currency("USD",9.22F);
		float theRate = testCurrency.getRate();
		
		assertEquals(9.22F, theRate, 0.001);
	}
	
	@Test
	public void testCurrencyToString() {
		Currency c = new Currency("AUD", 6.6047F);
		String expected = "Currency [currencyCode=AUD, rate=6.6047]";
		
		assertEquals(expected, c.toString());
	}

}
