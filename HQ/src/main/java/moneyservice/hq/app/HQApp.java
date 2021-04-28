package moneyservice.hq.app;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import moneyservice.model.MoneyServiceSites;
import affix.java.project.moneyservice.*;

public class HQApp {

	// CONSTANTS
	static Scanner keyboard = new Scanner(System.in);
	private static final int EXIT = 0;
	private static final int PERIOD_MENU_MIN = EXIT;
	private static final int PERIOD_MENU_MAX = 3;
	private static final int SITE_MENU_MIN = EXIT;
	private static final int SITE_MENU_MAX = 5;

	public static void main(String[] args) {

		// get directory path for HQ project
		String HQdirPath = System.getProperty("user.dir");
		System.out.println(HQdirPath);	// DEBUG
		
		// iterate through Sites
		for(MoneyServiceSites aSite: MoneyServiceSites.values() ) {

			// get transaction directory path for each site
			String siteDirPath = HQdirPath + File.separator + "Transactions" + File.separator + aSite;
			// System.out.println(siteDirPath);	// DEBUG
			
			try (Stream<Path> walk = Files.walk(Paths.get(siteDirPath))) {

				List<String> filenameList = walk
						.map(x -> x.toString())
						.filter(f -> f.endsWith(".ser"))
						.collect(Collectors.toList());

				filenameList.forEach(System.out::println);
				List<String> files = filenameList
						.stream()
						.map(f -> f.substring(f.indexOf("\\bTransactions/SOUTH/\\b")+1))
						.collect(Collectors.toList());
				files.forEach(System.out::println);

			} catch (IOException e) {
				// System.out.println("Did not find path " + siteDirPath);
			}
		}
		int siteChoice = presentSiteMenu();
		for(MoneyServiceSites site : MoneyServiceSites.values()) {
			if(siteChoice == site.getNumVal()) {
				System.out.println("Entered choice for " + site.getName());
				int menuChoice = presentPeriodMenu();
				Optional<LocalDate> date = enterStartDateForPeriod();
				
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
		System.out.println();
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
		System.out.println();
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
	 * @return
	 */
	private static int presentCurrencyMenu() {
		return 0;
	}

}

