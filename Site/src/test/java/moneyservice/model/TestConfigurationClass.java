package moneyservice.model;

import static org.junit.Assert.*;
import java.io.File;
import java.time.LocalDate;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestConfigurationClass {
	
	private String configFile = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01.txt";

	/**
	 * Test Configuration class before setting the configuration
	 */
	@Test
	public void firstTestTransactionFee() {
		assertTrue(Configuration.getTransactionFee()>0.004F);
		assertTrue(Configuration.getTransactionFee()<0.006F);
	}
	@Test
	public void firstTestSellRate() {
		assertTrue(Configuration.getSellRate()>1.004F);
		assertTrue(Configuration.getSellRate()<1.006F);
	}
	@Test
	public void firstTestBuyRate() {
		assertTrue(Configuration.getBuyRate()>0.994F);
		assertTrue(Configuration.getBuyRate()<0.996F);
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
