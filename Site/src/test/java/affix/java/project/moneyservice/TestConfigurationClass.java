package affix.java.project.moneyservice;

import static org.junit.Assert.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestConfigurationClass {
	
	private String configFile = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01.txt";
	private String badCurrencyFile = "TestConfigFiles" + File.separator + "TestConfig_2021-04-01_WrongCurrency.txt";

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

	@Test
	public void testParseConfigFileException() {
		boolean stored = Configuration.parseConfigFile("testConfig/\b.ser");
		
		assertFalse(stored);
	}
	
	
	
	@Test
	public void testParseCurrencyFileExceptionIO() {
		Configuration.parseConfigFile(badCurrencyFile);	
		assertTrue(Configuration.getCurrencies().isEmpty());
	}
	
	@Test
	public void testParseCurrencyFileExceptionNumberFormat() {
		boolean stored =Configuration.parseConfigFile("TestConfigFiles/TestConfig_2021-04-02.txt");
		
	}
	
	
	
	@Test
	public void testParseCurrencyFileExceptionDate() {
		boolean stored = Configuration.parseConfigFile("TestConfigFiles/TestConfig_2021-04-03.txt");
	}
	
	@Test
	public void testPathConfigurations() {
		boolean stored =Configuration.parseConfigFile("TestConfigFiles/TestConfig_2021-04-05_WrongPaths.txt");
		assertTrue(stored);
	}
	
	@Test
	public void testPathConfigurationsEmpty() {
		boolean stored =Configuration.parseConfigFile("TestConfigFiles/TestConfig_2021-04-05_EmptyPaths.txt");
		assertTrue(stored);
	}
}

