package moneyservice.java.app;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCreateOrder {

	@Test
	public void testAddTwoNumber() {
		TestWorkFlow twf = new TestWorkFlow();
		int result = twf.addTwoNumbers(5,5);
		assertEquals(result, 10);
	}

}
