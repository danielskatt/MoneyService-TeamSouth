package affix.java.project.moneyservice;

import static org.junit.Assert.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestConfigurationClass {
	
	private String configFile = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01.txt";
	private String configFileException = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01a.txt";
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
	 * A file named CurrencyConfigTest_2021-04-01 needs to reside in directory:
	 * DailyRates/TestConfigFiles
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
		
		assertEquals(0.005F, Configuration.getTransactionFee(), 0.001);
	}
	
	@Test
	public void testSellRate() {
		
		assertEquals(1.005F, Configuration.getSellRate(), 0.001);
	}
	
	@Test
	public void testBuyRate() {
		
		assertEquals(0.995F, Configuration.getBuyRate(), 0.001);
	}
	
	@Test
	public void testLocalCurrency() {
		
		assertEquals("SEK", Configuration.getLOCAL_CURRENCY());
	}
	
	@Test
	public void testDate() {
		
		
		LocalDate currentDate = LocalDate.now();

		assertEquals(currentDate, Configuration.getCURRENT_DATE());
	}
	
	@Test
	public void testBoxOfCash() {
		
		assertFalse(Configuration.getBoxOfCash().isEmpty());
	}
	
	@Test
	public void testParseConfigFileException() {
		boolean test = Configuration.parseConfigFile("/");
		assertFalse(test);
	}

	@Test
	public void testParseConfigFileException2() {
		boolean test = Configuration.parseConfigFile(configFileException);
		assertTrue(test);
	}
	
	@Test
	public void testParseCurrencyFile() {
		Map<String,Currency> test = Configuration.parseCurrencyFile("TestConfigFiles/SOUTH/DETALJERAT RESULTAT_2021-04-01.txt");
		assertNotNull(test);
	}
}
