package moneyservice.model;

import static org.junit.Assert.*;

import org.junit.Test;

import affix.java.project.moneyservice.User;

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

}
