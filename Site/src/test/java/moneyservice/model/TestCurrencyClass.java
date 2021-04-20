package moneyservice.model;

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
		
		assertEquals(theRate,9,22F);
	}

}
