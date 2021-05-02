package moneyservice.hq.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
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
import moneyservice.model.HQ;
import moneyservice.model.MoneyServiceSites;

/**
 * This class works as HeadQuarter for Site(s). 
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

		// store the transaction in a map holding site name and date as key and a list of Transactions a value
		Map<String, List<Transaction>> siteTransactions = getTransactions();
		boolean exit = false;

		HQ theHQ = new HQ("HQ", siteTransactions);

		// user input for choosing which site to filter
		int siteChoice = presentSiteMenu();
		for(MoneyServiceSites site : MoneyServiceSites.values()) {
			if(siteChoice == site.getNumVal()) {
				if(theHQ.getSiteTransactions().containsKey(site.getName()) || site.getName().equalsIgnoreCase("ALL")) {
					int period = presentPeriodMenu();
					while(!exit) {
						Optional<LocalDate> startDate = enterStartDateForPeriod();
						Optional<LocalDate> endDate = setEndDate(period, startDate);
						List<String> availableCodes = theHQ.getCurrencyCodes(site.getName(), startDate.get(), endDate.get());
						Optional<String> currencyCode = presentCurrencyMenu(availableCodes);
						if(!currencyCode.isEmpty()) {
							if(currencyCode.get().equals("T*")) {
								theHQ.printTransactions(site.getName(), startDate.get(), endDate.get());
								exit = true;
							}
							else {
								theHQ.printStatistics(site.getName(), currencyCode.get(), startDate.get(), endDate.get());	
								exit = true;
							}
						}
						else {
							exit = false;
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param filenames
	 * @param aSite
	 * @return
	 */
	private static Map<String, List<Transaction>> getTransactions(){
		Map<String, List<Transaction>> siteTransactions = new TreeMap<String, List<Transaction>>();
		
		for(MoneyServiceSites aSite : MoneyServiceSites.values()) {
			List<String> filenames = getFilenames(aSite);
			// Add the files into a Map holding Site name and Date as key, example "SOUTH_2021-04-01" and the Transactions from the file name
			for(String filename : filenames) {
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
			
		}
		return siteTransactions;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	private static List<String> getFilenames(MoneyServiceSites aSite){
		List<String> filenameList = new ArrayList<String>();
		// get directory path for HQ project
		String HQdirPath = System.getProperty("user.dir");
		// get transaction directory path for each site
		String path = HQdirPath + File.separator + "Transactions" + File.separator + aSite;
		try (Stream<Path> walk = Files.walk(Paths.get(path))) {
			// Stream for getting the file names
			filenameList = walk
					.map(f -> f.toFile())				// Convert Path to File
					.map(f -> f.getName())				// Get File name from full path
					.filter(f -> f.endsWith(".ser"))	// Filter to the files that ends with ".ser"
					.sorted()							// Sort the files in name ascending order
					.collect(Collectors.toList());		// Collect them into a List of String

		} catch (IOException e) {
			// TODO: Add this to logging?
		}
		return filenameList;
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
		for(MoneyServiceSites site : MoneyServiceSites.values()) {
			if(!site.getName().equalsIgnoreCase("None")) {
				System.out.format("%d - %s%n", site.getNumVal(), site.getName());				
			}
		}
		System.out.format("0 - Exit%n%n");

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
	 * 
	 * @param period
	 * @param startDate
	 * @return
	 */
	private static Optional<LocalDate> setEndDate(int period, Optional<LocalDate> startDate){
		Optional<LocalDate> endDate = Optional.empty();
		switch(period){
		case 1:
			if(startDate.isPresent()) {
				endDate = startDate;							
			}
			break;
		case 2:
			if(startDate.isPresent()) {
				DayOfWeek day = startDate.get().getDayOfWeek();
				if(startDate.get().getDayOfMonth() - day.getValue() < 1) {
					int toFriday = 5 - day.getValue();
					endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().getDayOfMonth()+toFriday));
				}
				else {
					startDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().getDayOfMonth()-(day.getValue()-1)));
					if(startDate.get().getDayOfMonth() + 4 <= startDate.get().lengthOfMonth()) {
						endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().getDayOfMonth()+4));																		
					}
					else {
						endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().lengthOfMonth()));
					}
				}
			}
			break;
		case 3:
			if(startDate.isPresent()) {
				endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().lengthOfMonth()));							
			}
			break;
		case 0:
			break;
		default:
			break;
		}
		return endDate;
	}
	
	/**
	 * This method presents the available currencies and gets input for currency from user
	 * @return a String from user input holding the currency or ALL for all currencies
	 */
	private static Optional<String> presentCurrencyMenu(List<String> currencyCodes) {
		boolean exit = false;
		String input = null;
		while(!exit) {
			System.out.print("Available currency codes: ");
			if(currencyCodes.isEmpty()) {
				System.out.println("No available currencies...");
				exit = true;
			}
			else {
				for(String currency : currencyCodes) {
					System.out.print(currency + " ");
				}
				System.out.print("ALL ");
				input = keyboard.next();
				for(String match : currencyCodes) {
					if(input.equals(match) || input.equals("ALL") || input.equals("T*")) {
						exit = true;
					}
					else {
						System.out.println("Not a valid currency or code");
					}
				}				
			}
		}
		return input != null ? Optional.of(input) : Optional.empty();
	}
}

