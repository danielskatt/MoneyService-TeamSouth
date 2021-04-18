package moneyservice.java.model;

import static org.junit.Assert.*;

import org.junit.Test;

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
		String name = testUsers.getName();
		assertTrue(name.equals("Test"));  
	}

}
