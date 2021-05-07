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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Transaction;
import moneyservice.model.HQ;
import moneyservice.model.Period;

/**
 * This class works as HeadQuarter for Site(s). 
 * It collects all the statistics from the Site(s) and present it to the user depending on input
 */
public class HQApp {
	
	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}
	
	/**
	 * Constant keyboard a Scanner for keyboard input 
	 */
	static Scanner keyboard = new Scanner(System.in);

	/**
	 * Constant EXIT a int defining number for exiting the program
	 */
	private static final int EXIT = 0;
	
	/**
	 * Constant PERIOD_MENU_MIN a int defining lowest menu choice
	 */
	private static final int PERIOD_MENU_MIN = EXIT;
	
	/**
	 * Constant PERIOD_MENU_MAX a int defining highest menu choice
	 */
	private static final int PERIOD_MENU_MAX = 3;
	
	/**
	 * Constant PERIOD_MENU_MIN a int defining lowest menu choice
	 */
	private static final int SITE_MENU_MIN = EXIT;
	
	/**
	 * logger a Logger 
	 */
	private static Logger logger;

	/**
	 * Main for Money Service HQ
	 * @param args a String holding file name including path for configuration file
	 */
	public static void main(String[] args) {
		
		/*--- Set up configuration ------------------------------------------------*/

		if(args.length > 0) {	// Use argument as file name input to set up configuration (file name format = Configs/<filename>.txt)
			boolean ok = Configuration.parseConfigFile(args[0]);
      logger.info(args[0] + " read in as a program argument");

			if(!ok) {	// shut down program if error when trying to parse configuration file		
				logger.log(Level.SEVERE,"ERROR occured when trying to read configuration file and set up configuration!");
				System.exit(1);
			}		
		}
		else {	// if no argument for file name is supplied
			System.out.println("ERROR no configuration file was supplied!");
			System.exit(1);
		}

		// store the transaction in a map holding site name as key and a list of Transactions a value
		Map<String, List<Transaction>> siteTransactions = getTransactions();
		boolean exit = false;

		HQ theHQ = new HQ("HQ", siteTransactions, Configuration.getSites());
		
		boolean correctSiteReport = theHQ.checkCorrectnessSiteReport();
		
		if(!correctSiteReport) {
			logger.log(Level.SEVERE, "Not a correct SiteReport!");
		}

		// user input for choosing which site to filter
		String siteChoice = presentSiteMenu();
		logger.fine(siteChoice + " choosen as Site");
		for(String site : theHQ.getSites()) {
			if(site.equalsIgnoreCase(siteChoice) || siteChoice.equalsIgnoreCase("ALL")) {
				if(theHQ.getSiteTransactions().containsKey(site) || siteChoice.equalsIgnoreCase("ALL")) {
					while(!exit) {
						Period period = presentPeriodMenu();
						logger.fine(period + " choosen as Period");
						Optional<LocalDate> startDate = enterStartDateForPeriod();
						logger.fine(startDate + " choosen start date");
						startDate = setStartDate(period, startDate);
						Optional<LocalDate> endDate = setEndDate(period, startDate);
						logger.fine(endDate + " choosen end date");
						List<String> availableCodes = theHQ.getAvailableCurrencyCodes(site, startDate.get(), endDate.get());
						Optional<String> currencyCode = presentCurrencyMenu(availableCodes);
						logger.fine(currencyCode + " choosen as target currency");
						
						if(currencyCode.isPresent()) {
							System.out.println("-----------------------------------");
							System.out.println("Choice for statistics: ");
							System.out.println("Site: " + siteChoice.toUpperCase());
							System.out.println("Period: " + period.getName().toUpperCase() + " starting " + startDate.get());
							System.out.println("Currency: " + currencyCode.get());
							System.out.println("-----------------------------------");							
						}
						
						if(!currencyCode.isEmpty()) {
							if(currencyCode.get().equals("T*")) {
								theHQ.printTransactions(siteChoice, period, startDate.get(), endDate.get());
							}
							else {
								if(period.equals(Period.DAY)) {
									theHQ.printStatisticsDay(siteChoice, period, currencyCode.get(), availableCodes, startDate.get());										
								}
								else if(period.equals(Period.WEEK)) {
									theHQ.printStatisticsWeek(siteChoice, period, currencyCode.get(), availableCodes, startDate.get(), endDate.get());
								}
								else if(period.equals(Period.MONTH)) {
									theHQ.printStatisticsMonth(siteChoice, period, currencyCode.get(), availableCodes, startDate.get(), endDate.get());
								}
							}
							exit = true;
						}
						else {
							logger.log(Level.SEVERE, "currency code is empty!");
							exit = false;
						}
					}
				}
			}
		}
	}
	
	/**
	 * This method collects all the Transactions for each Site and put it in a Map
	 * @return {@code Map<String, List<Transaction>>} holding Site as Key and a List with all Transactions for each Site
	 */
	private static Map<String, List<Transaction>> getTransactions(){
		Map<String, List<Transaction>> siteTransactions = new TreeMap<String, List<Transaction>>();
		// get directory path for HQ project
		String HQdirPath = System.getProperty("user.dir");
		
		for(String aSite : Configuration.getSites()) {
			// get transaction directory path for each site
			String path = HQdirPath + File.separator + Configuration.getPathTransactions() + aSite;
			List<String> filenames = getFilenames(path, ".ser");
			// Add the files into a Map holding Site name and Date as key, example "SOUTH_2021-04-01" and the Transactions from the file name
			for(String filename : filenames) {
				// add correct path for file name
				filename = Configuration.getPathTransactions() + aSite.toUpperCase() + File.separator + filename;
				// get all the transactions from the file
				List<Transaction> transactions = MoneyServiceIO.readReportAsSer(filename);
				logger.finer("All transactions from "+filename+ " has been read in");
				
				if(siteTransactions.containsKey(aSite)) {
					List<Transaction> newList = siteTransactions.get(aSite);
					newList.addAll(transactions);
					siteTransactions.replace(aSite, newList);						
				}
				else {
					siteTransactions.putIfAbsent(aSite, transactions);
				}
			}
			
		}
		return siteTransactions;
	}
	
	/**
	 * This method gets all files in a specific path
	 * @param path a String with a Path to current folder
	 * @param extension a String defining file format
	 * @return {@code List<String>} with all the file names in the path for specific Site
	 */
	public static List<String> getFilenames(String path, String extension){
		List<String> filenameList = new ArrayList<String>();

		try (Stream<Path> walk = Files.walk(Paths.get(path))) {
			// Stream for getting the file names
			filenameList = walk
					.map(f -> f.toFile())				// Convert Path to File
					.map(f -> f.getName())				// Get File name from full path
					.filter(f -> f.endsWith(extension))	// Filter to the files that ends with ".ser"
					.sorted()							// Sort the files in name ascending order
					.collect(Collectors.toList());		// Collect them into a List of String

		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return filenameList;
	}
	
	/**
	 * Gets unsigned number from user input. If entry is not valid return value equals to -1
	 * @return num an int {@code >=} 0 OR -1 if input is invalid
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
	 * @return userSiteInput a String defining the chosen site
	 */
	private static String presentSiteMenu() {
		String site = "";
		// get user int input 
		int userSiteInput;
		List<String> sites = Configuration.getSites();

		do {
		// present site menu
		System.out.println("--------------------------------------------------");
		System.out.format("Choose a Site%n%n");
		for(int i = 0 ; i < sites.size() ; i++) {
			System.out.format("%d - %s%n",i+1, sites.get(i));							
		}
		System.out.format("%d - ALL%n", sites.size()+1);

		System.out.format("Enter your choice: ");

			userSiteInput = getInputUint();

			if(userSiteInput > sites.size()+1 || userSiteInput == -1) {
				System.out.println(userSiteInput + " is not a menu choice!");
			}

		}while(!(userSiteInput > SITE_MENU_MIN && userSiteInput <= sites.size()+1));
		
		if(userSiteInput == sites.size()+1) {
			site = "ALL";
		}
		else {
			site = sites.get(userSiteInput-1);			
		}

		return site;
	}

	/**
	 * This method gets user input for period 
	 * @return userPeriodInput a Period (enum) defining the chosen period
	 */
	private static Period presentPeriodMenu() {
		Period period = Period.NONE;

		// present period menu
		System.out.println("--------------------------------------------------");
		System.out.format("Choose a Period%n%n");
		for(Period p : Period.values()) {
			if(!p.getName().equalsIgnoreCase("None")) {
				System.out.format("%d - %s%n", p.getNumVal(), p.getName());				
			}
		}
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

		}while(!(userPeriodInput > PERIOD_MENU_MIN && userPeriodInput <= PERIOD_MENU_MAX));
		
		for(Period p : Period.values()) {
			if(p.getNumVal() == userPeriodInput) {
				period = p;
			}
		}

		return period;
	}
	
	/**
	 * This method gets user input for desired date (YYYY-MM-DD)
	 * @return LocalDate defining a date from user to filter statistics
	 */
	private static Optional<LocalDate> enterStartDateForPeriod() {
		boolean correctDate = false;
		Optional<LocalDate> startDate = Optional.empty();
		while(!correctDate) {
			System.out.print("Enter start day of Period (YYYY-MM-DD): ");
			try {
				startDate = Optional.of(LocalDate.parse(keyboard.next()));
				correctDate = true;
			}
			catch(DateTimeParseException dtpe) {
				logger.log(Level.WARNING, dtpe.getMessage() + " is not a valid date!");
				correctDate = false;
			}			
		}
		return startDate;
	}
	
	/**
	 * This method is used to set the end date depending on which period that is entered
	 * @param period a Period (enum) holding a Period from user input
	 * @param startDate a LocalDate holding information about the start date of the period
	 * @return an {@code Optional<LocalDate>} with the end date for period
	 */
	private static Optional<LocalDate> setEndDate(Period period, Optional<LocalDate> startDate){
		Optional<LocalDate> endDate = Optional.empty();
		switch(period.getName()){
		case "Day":
			if(startDate.isPresent()) {
				endDate = startDate;
			}
			break;
		case "Week":
			if(startDate.isPresent()) {
				DayOfWeek day = startDate.get().getDayOfWeek();
				// check if there is a month break in beginning of week
				if(startDate.get().getDayOfMonth() - day.getValue() < 1) {
					int toFriday = 5 - day.getValue();
					// keep start day as it is and set end date to Friday same week
					endDate = Optional.of(startDate.get().plusDays(toFriday));
					
				}
				else {
					startDate = Optional.of(startDate.get().minusDays(day.getValue()-1));
					// check if there is a month break between start date and Friday same week
					if(startDate.get().getDayOfMonth() + 4 <= startDate.get().lengthOfMonth()) {
						endDate = Optional.of(startDate.get().plusDays(4));
						
					}
					else {
						endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().lengthOfMonth()));
						
					}
				}
			}
			break;
		case "Month":
			if(startDate.isPresent()) {
				endDate = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), startDate.get().lengthOfMonth()));							
				
			}
			break;
		case "None":
			break;
		default:
			break;
		}
		return endDate;
	}
	
	/**
	 * This method is used to set the start date depending on which period that is entered
	 * @param period a Period (enum) holding a Period from user input
	 * @param startDate a LocalDate holding information about the start date of the period
	 * @return an {@code Optional<LocalDate>} with the new start date for period
	 */
	private static Optional<LocalDate> setStartDate(Period period, Optional<LocalDate> startDate){
		Optional<LocalDate> date = Optional.empty();
		switch(period.getName()){
		case "Day":
			if(startDate.isPresent()) {
				date = startDate;
				
			}
			break;
		case "Week":
			if(startDate.isPresent()) {
				DayOfWeek day = startDate.get().getDayOfWeek();
				// check if there is a month break in beginning of week
				if(startDate.get().getDayOfMonth() - day.getValue() < 1) {
					date = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), 1));
					
				}
				else {
					date = Optional.of(startDate.get().minusDays(day.getValue()-1));
					
				}
			}
			break;
		case "Month":
			if(startDate.isPresent()) {
				date = Optional.of(LocalDate.of(startDate.get().getYear(), startDate.get().getMonthValue(), 1));
				
			}
			break;
		case "None":
			break;
		default:
			break;
		}
		return date;
	}
	
	/**
	 * This method presents the available currencies and gets input for currency from user
	 * @param currencyCodes a {@code List<String>} with all the available currency codes
	 * @return a String from user input holding a specific currency or ALL for all currencies
	 */
	private static Optional<String> presentCurrencyMenu(List<String> currencyCodes) {
		boolean exit = false;
		String input = null;
		while(!exit) {
			System.out.print("Available currency codes: ");
			if(currencyCodes.isEmpty()) {
				logger.log(Level.WARNING,"No available currencies in List");
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
				}				
				if(!exit) {
					logger.log(Level.WARNING, "Not a valid currency or code");
				}
			}
		}
		return input != null ? Optional.of(input) : Optional.empty();
	}
}

