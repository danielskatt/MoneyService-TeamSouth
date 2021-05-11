package moneyservice.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;
import moneyservice.hq.app.HQApp;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class TestHQ {
	
	@Test 
	public void setUpTest() {
		boolean configurationOK = Configuration.parseConfigFile("Configs/ProjectConfigHQ_2021-04-01.txt");
		assertTrue(configurationOK);
	}

	@Test
	public void testSiteReport() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		boolean ok = hq.checkCorrectnessSiteReport();
		assertTrue(ok);
	}
	
	@Test
	public void testAvailableCurrencyCodes1() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		List<String> currencyCodes = hq.getAvailableCurrencyCodes("SOUTH", startDate, startDate);
		assertEquals(9, currencyCodes.size());
	}
	
	@Test
	public void testAvailableCurrencyCodes2() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		List<String> currencyCodes = hq.getAvailableCurrencyCodes("ALL", startDate, startDate);
		assertEquals(10, currencyCodes.size());
	}
	
	@Test
	public void testStatisticsDaySellUSD() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double sell = hq.getStatisticsDay("SOUTH", siteTransactions.get("SOUTH"), "USD", TransactionMode.SELL, startDate);
		assertEquals(4393, sell, 1.0);
	}
	
	@Test
	public void testStatisticsDayBuyUSD() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double buy = hq.getStatisticsDay("SOUTH", siteTransactions.get("SOUTH"), "USD", TransactionMode.BUY, startDate);
		assertEquals(13919, buy, 1.0);
	}
	
	@Test
	public void testStatisticsDaySellALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double sell = hq.getStatisticsDay("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.SELL, startDate);
		assertEquals(30030, sell, 2.0);
	}
	
	@Test
	public void testStatisticsDayBuyALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double buy = hq.getStatisticsDay("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.BUY, startDate);
		assertEquals(33954, buy, 1.0);
	}
	
	@Test
	public void testStatisticsAmountDaySellEUR() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double sell = hq.getStatisticsAmountDay("SOUTH", siteTransactions.get("SOUTH"), "EUR", TransactionMode.SELL, startDate);
		assertEquals(1550, sell, 1.0);
	}
	
	@Test
	public void testStatisticsAmountDayBuyEUR() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double buy = hq.getStatisticsAmountDay("SOUTH", siteTransactions.get("SOUTH"), "EUR", TransactionMode.BUY, startDate);
		assertEquals(0, buy, 1.0);
	}
	
	@Test
	public void testStatisticsAmountDaySellALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double sell = hq.getStatisticsAmountDay("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.SELL, startDate);
		assertEquals(5250, sell, 1.0);
	}
	
	@Test
	public void testStatisticsAmountDayBuyALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-01");
		double buy = hq.getStatisticsAmountDay("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.BUY, startDate);
		assertEquals(5050, buy, 1.0);
	}
	
	@Test
	public void testStatisticsPeriodSellGBP() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-07");
		LocalDate endDate = LocalDate.parse("2021-04-08");
		double sell = hq.getStatisticsPeriod("SOUTH", siteTransactions.get("SOUTH"), "GBP", TransactionMode.SELL, startDate, endDate);
		assertEquals(26305, sell, 1.0);
	}
	
	@Test
	public void testStatisticsPeriodBuyGBP() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-07");
		LocalDate endDate = LocalDate.parse("2021-04-08");
		double buy = hq.getStatisticsPeriod("SOUTH", siteTransactions.get("SOUTH"), "GBP", TransactionMode.BUY, startDate, endDate);
		assertEquals(24303, buy, 1.0);
	}
	
	@Test
	public void testStatisticsPeriodSellALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-07");
		LocalDate endDate = LocalDate.parse("2021-04-08");
		double sell = hq.getStatisticsPeriod("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.SELL, startDate, endDate);
		assertEquals(65103, sell, 1.0);
	}
	
	@Test
	public void testStatisticsPeriodBuyALL() {
		Map<String, List<Transaction>> siteTransactions = HQApp.getTransactions();
		HQ hq = new HQ("HQ", siteTransactions, Configuration.getSites());
		LocalDate startDate = LocalDate.parse("2021-04-07");
		LocalDate endDate = LocalDate.parse("2021-04-08");
		double buy = hq.getStatisticsPeriod("SOUTH", siteTransactions.get("SOUTH"), "ALL", TransactionMode.BUY, startDate, endDate);
		assertEquals(82295, buy, 1.0);
	}
	
	@Test
	public void testPeriod1() {
		Period p = Period.DAY;
		assertEquals(Period.DAY.toString(), p.getName().toUpperCase());
	}
	
	@Test
	public void testPeriod2() {
		Period p = Period.WEEK;
		assertEquals(2, p.getNumVal());
	}

}