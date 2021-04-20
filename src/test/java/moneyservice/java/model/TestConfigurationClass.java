package moneyservice.java.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestConfigurationClass {

	@Test
	public void test() {
		String configFile = "ProjectConfig_2021-04-01.txt";
		Configuration.parseConfigFile(configFile);
		assertEquals("SEK", Configuration.getLOCAL_CURRENCY());
	}

}
