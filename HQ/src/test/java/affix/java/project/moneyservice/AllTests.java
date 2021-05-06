package affix.java.project.moneyservice;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
	TestCurrencyClass.class, 
	TestOrderClass.class,
	TestTransactionClass.class,
	TestMoneyServiceIOCLass.class,
	TestConfigurationClass.class,
})
public class AllTests {;}
