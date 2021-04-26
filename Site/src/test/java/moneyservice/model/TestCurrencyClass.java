package moneyservice.model;

import static org.junit.Assert.*;

import org.junit.Test;

import affix.java.project.moneyservice.Currency;

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
		Currency currency = new Currency("test", 1.03F);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testCurrencyConstructor3() {
		Currency currency = new Currency("TEST", 1.03F);
	}
	@Test (expected = IllegalArgumentException.class)
	public void testCurrencyConstructor4() {
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
		
		assertTrue(theRate>9.21F);
		assertTrue(theRate<9.23F);
	}

}
