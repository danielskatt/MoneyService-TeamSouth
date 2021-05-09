package affix.java.project.moneyservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(
	{ 
		TestConfigurationClass.class, 
		TestCurrencyClass.class, 
		TestMoneyServiceIOCLass.class,
		TestOrderClass.class, 
		TestSiteClass.class, 
		TestTransactionClass.class, 
		TestUserClass.class 
	}
)
public class AllTests {;}
