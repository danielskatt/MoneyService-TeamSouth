package moneyservice.hq.app;

import java.util.Scanner;

public class HQApp {
	
	// CONSTANTS
	static Scanner keyboard = new Scanner(System.in);
	private static final int EXIT = 0;
	private static final int PERIOD_MENU_MIN = EXIT;
	private static final int PERIOD_MENU_MAX = 3;

	public static void main(String[] args) {
		// Test method for present period menu
		int userPeriodInput = presentPeriodMenu();
		System.out.format("%d", userPeriodInput);

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

}

