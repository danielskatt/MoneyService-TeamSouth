package main.java.moneyservice.java.app;

import java.time.LocalDate;
import java.util.Map;

public class Configuration {
	static final float TRANSACTION_FEE = 0.005F;
	static String LOCAL_CURRENCY;							// SEK
	static final float SELL_RATE = 1 + TRANSACTION_FEE;		// 1.005
	static final float BUY_RATE = 1 - TRANSACTION_FEE;		// 0.995
	static LocalDate CURRENT_DATE;				// todays Date in ISO standard
	private static String currencyConfigFile;	// filename format <CurrencyConfig_<Date in ISO standard>.txt>
	static Map<String, Integer> boxOfCash;		// Map<currencyCode, amount>
	static Map<String, Currency> currencies;	// Map<currencyCode, object Currency>
	
	public static void parseConfigFile(String filename) {
		
	}
}
