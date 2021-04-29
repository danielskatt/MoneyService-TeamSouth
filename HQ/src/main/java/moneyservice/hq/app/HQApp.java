package moneyservice.hq.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;
import moneyservice.hq.app.*;
import moneyservice.model.MoneyServiceSites;

/**
 * This class works as HeadQuarter for one or more Site(s). 
 * It collects all the statistics from the Site(s) and present it 
 * to the user depending on input
 */
public class HQApp {
	
	static Scanner keyboard = new Scanner(System.in);

	// CONSTANTS
	private static final int EXIT = 0;
	private static final int PERIOD_MENU_MIN = EXIT;
	private static final int PERIOD_MENU_MAX = 3;
	private static final int SITE_MENU_MIN = EXIT;
	private static final int SITE_MENU_MAX = 5;

	public static void main(String[] args) {

		// get directory path for HQ project
		String HQdirPath = System.getProperty("user.dir");

		// store the transaction in a map holding site name and date as key and a list of Transactions a value
		Map<String, List<Transaction>> siteTransactions = new TreeMap<String, List<Transaction>>();
		Map<String, List<String>> siteFilenames = new TreeMap<String, List<String>>();

		// iterate through all Sites
		for(MoneyServiceSites aSite: MoneyServiceSites.values() ) {

			// get transaction directory path for each site
			String siteDirPath = HQdirPath + File.separator + "Transactions" + File.separator + aSite;

			try (Stream<Path> walk = Files.walk(Paths.get(siteDirPath))) {

				// Stream for getting the file names
				List<String> filenameList = walk
						.map(f -> f.toFile())				// Convert Path to File
						.map(f -> f.getName())				// Get File name from full path
						.filter(f -> f.endsWith(".ser"))	// Filter to the files that ends with ".ser"
						.sorted()							// Sort the files in name ascending order
						.collect(Collectors.toList());		// Collect them into a List of String

				siteFilenames.putIfAbsent(aSite.getName(), filenameList);

				// Add the files into a Map holding Site name and Date as key, example "SOUTH_2021-04-01" and the Transactions from the file name
				for(String filename : filenameList) {
					// add correct path for file name
					filename = "../HQ/Transactions/" + aSite.getName().toUpperCase() + "/" + filename;
					// get all the transactions from the file
					List<Transaction> transactions = MoneyServiceIO.readReportAsSer(filename);
					// shorten the file name to site name and date for storing as key in map
					String key = aSite.getName();
					if(siteTransactions.containsKey(key)) {
						List<Transaction> newList = siteTransactions.get(key);
						newList.addAll(transactions);
						siteTransactions.replace(key, newList);						
					}
					else {
						siteTransactions.putIfAbsent(key, transactions);
					}
				}

			} catch (IOException e) {
				// TODO: Add this to logging?
			}
		}

		// user input for choosing which site to filter
		int siteChoice = presentSiteMenu();
		for(MoneyServiceSites site : MoneyServiceSites.values()) {
			if(siteTransactions.containsKey(site.getName())) {
				List<Transaction> transactions = siteTransactions.get(site.getName());		
				// TODO - Remove null
				LocalDate endDate = null;
				if(siteChoice == site.getNumVal()) {
					System.out.println("Entered choice for " + site.getName());
					int period = presentPeriodMenu();
					Optional<LocalDate> startDate = enterStartDateForPeriod();
					switch(period){
					case 1:
						endDate = startDate.get();
						break;
					case 2:
						// TODO - Change this to the last day of week
						endDate = LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().getDayOfMonth()+5);
						break;
					case 3:
						endDate = LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().lengthOfMonth());
						break;
					case 0:
						break;
					default:
						break;
					}
					List<String> availableCodes = getCurrencyCodes(transactions, startDate.get(), endDate);
					String currencyCode = presentCurrencyMenu(availableCodes);
					if(currencyCode.equals("T*")) {
						printTransactions(transactions, startDate.get());
					}
					else {
						filterStatistics(transactions, currencyCode, startDate.get(), endDate);					
					}
				}

			}
		}
	}

	/**
	 * Gets unsigned number from user input. If entry is not valid return value equals to -1
	 * @return num an int >= 0 OR -1 if input is invalid
	 */
	private static int getInputUint() {
		int num;
		final int errorNo = -1;

		if(keyboard.hasNextInt()) {
			num = keyboard.nextInt();
			if(num < 0) {	// check if unsigned 
				System.out.println(num + " is not a valid number!");
				num = errorNo;
			}
			return num;
		}

		String input = keyboard.next();
		System.out.println(input + " is not a valid number!");
		return errorNo;
	}

	/**
	 * This method gets user input for choice of Site
	 * @return userSiteInput an int defining the choosen site:
	 * 1 = North
	 * 2 = East
	 * 3 = Center
	 * 4 = South
	 * 5 = All
	 */
	private static int presentSiteMenu() {

		// present site menu
		System.out.println("--------------------------------------------------");
		System.out.format("Choose a Site%n%n");
		System.out.format("1 - North%n");
		System.out.format("2 - East%n");
		System.out.format("3 - Center%n");
		System.out.format("4 - South%n");
		System.out.format("5 - All%n");
		System.out.format("0 - Exit%n%n");	// EXIT

		System.out.format("Enter your choice: ");

		// get user int input 
		int userSiteInput;
		do {
			userSiteInput = getInputUint();

			if(userSiteInput> SITE_MENU_MAX) {

				System.out.println(userSiteInput + " is not a menu choice!");
				System.out.format("%nEnter your choice (%d-%d): ", SITE_MENU_MIN, SITE_MENU_MAX);
			}

			if(userSiteInput == -1) {
				System.out.format("%nEnter your choice (%d-%d): ", SITE_MENU_MIN, SITE_MENU_MAX);
			}

		}while(!(userSiteInput >= SITE_MENU_MIN && userSiteInput <= SITE_MENU_MAX));

		return userSiteInput;
	}

	/**
	 * This method gets user input for period 
	 * @return userPeriodInput an int defining the choosen period:
	 *  0 = exit
	 *  1 = day
	 *  2 = week
	 *  3 = month
	 */
	private static int presentPeriodMenu() {

		// present period menu
		System.out.println("--------------------------------------------------");
		System.out.format("Choose a Period%n%n");
		System.out.format("1 - Day%n");
		System.out.format("2 - Week%n");
		System.out.format("3 - Month%n");
		System.out.format("0 - Exit%n");	// EXIT

		System.out.println();
		System.out.format("Enter your choice: ");

		// get user int input 
		int userPeriodInput;
		do {
			userPeriodInput = getInputUint();

			if(userPeriodInput> PERIOD_MENU_MAX) {	// int is not a valid menu choice

				System.out.println(userPeriodInput + " is not a menu choice!");
				System.out.format("%nEnter your choice (%d-%d): ", PERIOD_MENU_MIN, PERIOD_MENU_MAX);
			}

			if(userPeriodInput == -1) {		// invalid input
				System.out.format("%nEnter your choice (%d-%d): ", PERIOD_MENU_MIN, PERIOD_MENU_MAX);
			}

		}while(!(userPeriodInput >= PERIOD_MENU_MIN && userPeriodInput <= PERIOD_MENU_MAX));

		return userPeriodInput;
	}
	
	/**
	 * This method gets user input for desired date (YYYY-MM-DD)
	 * @return LocalDate - a date from user to filter statistics
	 */
	private static Optional<LocalDate> enterStartDateForPeriod() {
		boolean correctDate = false;
		Optional<LocalDate> startDate = Optional.empty();
		while(!correctDate) {
			System.out.print("Enter start day of Period: ");
			try {
				startDate = Optional.of(LocalDate.parse(keyboard.next()));
				correctDate = true;
			}
			catch(DateTimeParseException dtpe) {
				System.out.println(dtpe.getMessage() + " is not a valid date!");
				correctDate = false;
			}			
		}
		return startDate;
	}
	
	/**
	 * This method gets all available currency codes from a map
	 * @param transactions - a Map with all Transaction(s)
	 * @return - A List with String of Currency codes
	 */
	private static List<String> getCurrencyCodes(List<Transaction> transactions, LocalDate startDate, LocalDate endDate){		
		// get all the available currency codes with no doubles
		List<String> availableCodes = transactions
				.stream()							// start a stream
				.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate)) // &&
//						t.getTimeStamp().toLocalDate().isAfter(startDate) &&
//						t.getTimeStamp().toLocalDate().isBefore(endDate) &&
//						t.getTimeStamp().toLocalDate().isEqual(endDate))
				.map(t -> t.getCurrencyCode())		// convert the stream to only handle currency codes
				.distinct()							// sort the currency code in alphabetic order
				.collect(Collectors.toList());		// collect all available element to a List
		
		return availableCodes;
	}
	
	/**
	 * This method presents the available currencies and gets input for currency from user
	 * @return a String from user input holding the currency or ALL for all currencies
	 */
	private static String presentCurrencyMenu(List<String> currencyCodes) {
		boolean correctCode = false;
		String input = "";
		while(!correctCode) {
			System.out.print("Available currency codes: ");
			for(String currency : currencyCodes) {
				System.out.print(currency + " ");
			}
			System.out.print("ALL ");
			input = keyboard.next();
			for(String match : currencyCodes) {
				if(input.equals(match) || input.equals("ALL") || input.equals("T*")) {
					correctCode = true;
				}
			}
		}
		return input;
	}
	
	/**
	 * This method filter the available statistics and present it to the user
	 * @param allTransactions - A List with all Transactions
	 * @param currencyCode - A String holding the desired currency code or ALL
	 * @param startDate - a LocalDate holding information about which period the user want to filter the statistics
	 * @param endDate - a LocalDate holding information about the start date of the period
	 */
	private static void filterStatistics(List<Transaction>transactions, String currencyCode, LocalDate startDate, LocalDate endDate) {
		IntSummaryStatistics sell;
		IntSummaryStatistics buy;
		if(currencyCode.equals("ALL")) {
			sell = transactions
					.stream()
					.filter(cc -> cc.getMode().equals(TransactionMode.SELL))
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate)) // &&
//							t.getTimeStamp().toLocalDate().isAfter(startDate) &&
//							t.getTimeStamp().toLocalDate().isBefore(endDate) &&
//							t.getTimeStamp().toLocalDate().isEqual(endDate))
					.collect(Collectors.summarizingInt(Transaction::getAmount));
			
			buy = transactions
					.stream()
					.filter(cc -> cc.getMode().equals(TransactionMode.BUY))
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate)) // &&
//							t.getTimeStamp().toLocalDate().isAfter(startDate) &&
//							t.getTimeStamp().toLocalDate().isBefore(endDate) &&
//							t.getTimeStamp().toLocalDate().isEqual(endDate))
					.collect(Collectors.summarizingInt(Transaction::getAmount));
		}
		else {
			sell = transactions
					.stream()
					.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
					.filter(cc -> cc.getMode().equals(TransactionMode.SELL))
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate)) // && 
//							t.getTimeStamp().toLocalDate().isAfter(startDate) &&
//							t.getTimeStamp().toLocalDate().isBefore(endDate) &&
//							t.getTimeStamp().toLocalDate().isEqual(endDate))
					.collect(Collectors.summarizingInt(Transaction::getAmount));
			
			buy = transactions
					.stream()
					.filter(cc -> cc.getCurrencyCode().equals(currencyCode))
					.filter(cc -> cc.getMode().equals(TransactionMode.BUY))
					.filter(t -> t.getTimeStamp().toLocalDate().isEqual(startDate)) // && 
//							t.getTimeStamp().toLocalDate().isAfter(startDate) &&
//							t.getTimeStamp().toLocalDate().isBefore(endDate) &&
//							t.getTimeStamp().toLocalDate().isEqual(endDate))
					.collect(Collectors.summarizingInt(Transaction::getAmount));			
		}
		
		System.out.println("Total   " + TransactionMode.SELL.name() + "  " + sell.getSum() + " SEK");
		System.out.println("Total   " + TransactionMode.BUY.name() + "  " + buy.getSum() + " SEK");
		System.out.println("Profit " + (sell.getSum() - buy.getSum()) + " SEK");
	}
	
	/**
	 * Print out all the transaction for the date provided
	 * @param transactions - a Map with all the transactions
	 * @param date - a start date for filtering the transactions
	 */
	private static void printTransactions(List<Transaction> transactions, LocalDate date) {
		List<Transaction> allTransactions = transactions
				.stream()													// start a stream of the values
				.filter(t -> t.getTimeStamp().toLocalDate().equals(date))	// filter out the Transaction(s) that matches the date input
				.distinct()													// make sure only has one of each element
				.collect(Collectors.toList());								// collect all the elements to a List
		
		allTransactions.forEach(System.out::println);
	}

}

