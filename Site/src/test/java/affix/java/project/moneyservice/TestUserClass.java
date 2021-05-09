package affix.java.project.moneyservice;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import moneyservice.site.app.User;


public class TestUserClass {

	@Test
	public void testUserConstructor1() {
		String name = "Test";
		User testUser = new User(name);
		assertNotNull(testUser);
	}
	
	@Test
	public void testGetName() {
		User testUser = new User("Test");
		String name = testUser.getName();
		assertTrue(name.equals("Test"));  
	}

	@Test
	public void testCreateOrder1() {
		User testUser = new User("Test");
		Optional<Order> o = testUser.createOrderRequest();
		
		assertTrue(o.isPresent());
	}
}
