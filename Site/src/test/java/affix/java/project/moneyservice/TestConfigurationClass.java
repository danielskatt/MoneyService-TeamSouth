package affix.java.project.moneyservice;

import static org.junit.Assert.*;
import java.io.File;
import java.time.LocalDate;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import affix.java.project.moneyservice.Configuration;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestConfigurationClass {
	
	private String configFile = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01.txt";

	/**
	 * Test Configuration class before setting the configuration
	 */
	@Test
	public void firstTestTransactionFee() {
		
		assertEquals(0.005F, Configuration.getTransactionFee(), 0.001);
	}
	
	@Test
	public void firstTestSellRate() {

		assertEquals(1.005F, Configuration.getSellRate(), 0.001);
	}
	
	@Test
	public void firstTestBuyRate() {
		assertEquals(0.995F, Configuration.getBuyRate(), 0.001);
	}
	
	/**
	 * Set up the configuration by running the configFile
	 */
	@Test
	public void setUpConfiguration() {
		Configuration.parseConfigFile(configFile);
		assertNotNull(Configuration.getLOCAL_CURRENCY());
	}
	
	/**
	 * Test so all the methods and attributes has been run and stored
	 */
	@Test
	public void testTransactionFee() {
		assertTrue(Configuration.getTransactionFee()>0.004F);
		assertTrue(Configuration.getTransactionFee()<0.006F);
	}
	@Test
	public void testSellRate() {
		assertTrue(Configuration.getSellRate()>1.004F);
		assertTrue(Configuration.getSellRate()<1.006F);
	}
	@Test
	public void testBuyRate() {
		assertTrue(Configuration.getBuyRate()>0.994F);
		assertTrue(Configuration.getBuyRate()<0.996F);
	}
	@Test
	public void testLocalCurrency() {
		assertEquals("SEK", Configuration.getLOCAL_CURRENCY());
	}
	@Test
	public void testDate() {
		String date = configFile.substring(configFile.indexOf("_")+1, configFile.indexOf("."));
		LocalDate currentDate = LocalDate.parse(date);
		assertEquals(currentDate, Configuration.getCURRENT_DATE());
	}
	@Test
	public void testCurrencyConfigFile() {
		assertFalse(Configuration.getCurrencyConfigFile().isBlank());
	}
	@Test
	public void testBoxOfCash() {
		assertFalse(Configuration.getBoxOfCash().isEmpty());
	}
	@Test
	public void testCurrencies() {
		assertFalse(Configuration.getCurrencies().isEmpty());
	}

}
