package moneyservice.site.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.Site;
import affix.java.project.moneyservice.Transaction;

/** 
 *  Money Service Site Application
 *  Read Configuration file for setting up the configuration of the Application. 
 *  Application for Money Service Site has two different modes test mode and regular mode. 
 *  Test mode is accessed through key = value pair testmode = true in configuration file. 
 *  Test mode generates 25 random Orders for Site for each day and creates a daily report.
 *  Regular mode contains menus for both Site and customer.
 *  Site will be able to create daily report and customer is able to create orders. 
 */
public class MoneyServiceApp {

	/**
	 * Constant keyboard a Scanner for keyboard input 
	 */
	static Scanner keyboard = new Scanner(System.in);

	/**
	 * Constant EXIT a int defining number for exiting the program
	 */
	private static final int EXIT = 0;

	/**
	 * Constant MENU_MIN a int defining lowest menu choice
	 */
	private static final int MENU_MIN = EXIT;

	/**
	 * Constant MENU_MAX a int defining highest menu choice
	 */
	private static final int MENU_MAX = 2;

	/**
	 * Constant NO_OF_ORDERS an int defining number of random generated orders to create in test mode
	 */
	private static final int NO_OF_ORDERS = 25;

	/**
	 * logger a Logger 
	 */
	private static Logger logger;

	/**
	 * Main for Money Service Site
	 * @param args a String holding file name including path for configuration file
	 */
	public static void main(String[] args) {

		logger = Logger.getLogger("affix.java.project.moneyservice");

		/*--- Set up configuration ------------------------------------------------*/

		if(args.length > 0) {	// Use argument as file name input to set up configuration (file name format = Configs/<filename>.txt)
			logger.info(args[0] + " read in as a program argument");
			boolean ok = Configuration.parseConfigFile(args[0]);

			if(!ok) {	// shut down program if error when trying to parse configuration file		
				logger.log(Level.SEVERE,"ERROR occured when trying to read configuration file and set up configuration!");
				System.exit(1);
			}		
		}
		else {	// if no argument for file name is supplied
			logger.log(Level.SEVERE,"ERROR no configuration file was supplied!");
			System.exit(1);
		}
		
		/*--- Create folder to store transactions ---------------------------------*/

		File path = new File(Configuration.getPathTransactions());		// create path for transactions directory
		path.mkdir();													// creates a directory if it does not exist

		/*--- Create User object --------------------------------------------------*/
		User user = new User("User 1");
		logger.fine("User " + user.getName() + " created!");

		/*--- Set up site ---------------------------------------------------------*/ 
		String siteName = Configuration.getSiteName();	// get Site name
		logger.fine("Site: " + siteName);
		Map<String, Double> boxOfCash = Configuration.getBoxOfCash();
		Map<String, Currency> currencies = Configuration.getCurrencies();
		Site site;

		try {
			site = new Site(siteName, boxOfCash, currencies);

			if(!Configuration.isTestMode()) {	// if test mode is configured to false, run CLI
				logger.info("Running CLI");
				boolean exit = false;
				do {
					System.out.format("%n*** Money Service Menu --------------------%n");
					System.out.println("1 - Site menu");
					System.out.println("2 - User menu");
					System.out.println("0 - Exit application");
					
					int menuInput;
					do {
						System.out.format("%nEnter your choice (%d-%d): ", MENU_MIN, MENU_MAX);
						menuInput = getInputUint();
						if(menuInput> MENU_MAX) {
							System.out.println(menuInput + " is not a menu choice!");
						}	
					}while(!(menuInput >= MENU_MIN && menuInput <= MENU_MAX));

					switch(menuInput) {
					case 0:		// Exit program
						exit = true;
						break;
					case 1: 	// Site menu
						presentSiteMenu(site);

						break;
					case 2:		// User menu
						presentUserMenu(user, site);
						break;
					}
				}while(!exit);
			}
			else {	// if test mode is configured to true, run test
				logger.info("Running TestMode!");
				// generate random orders
				multipleOrder(user, NO_OF_ORDERS, site); 

				// shut down service stores transactions into file
				site.shutDownService(Configuration.getFileNameTransactionsReport());

				// print transactions that has been read from file
				List<Transaction> randTransactionList = MoneyServiceIO.readReportAsSer(Configuration.getFileNameTransactionsReport());
				for(Transaction t : randTransactionList) {
					System.out.println(t.toString());
				}
			}

			// shut down service
			site.shutDownService(Configuration.getFileNameTransactionsReport());
			logger.info("End of program!");
		}
		catch (IllegalArgumentException e){
			logger.log(Level.SEVERE, e.getMessage());
		}
		catch(NullPointerException e){
			// write error message (date error when date does not exist)
			logger.log(Level.SEVERE, "Date does not exist!");
		}
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
	 * Helper method to create multiple orders per day
	 * @param user a User defining the customer for Money Service Site
	 * @param numberOfOrders an int defining the number of orders to generate
	 * @param site a Site defining the Site that handles the Orders created
	 */
	public static void multipleOrder(User user, int numberOfOrders, Site site) {

		// generate random orders
		for(int i = 0; i < numberOfOrders; i++) {		// loop to generate random orders
			Optional<Order> opRandOrder = user.createOrderRequest();	// create a random order

			if(opRandOrder.isPresent()) {
				Order randOrder = opRandOrder.get();
				logger.fine(randOrder + " has been placed");
				writeOrderAsText(randOrder);	// write order to file
				boolean orderApproved = handleOrder(randOrder, site);

				if(!orderApproved) {
					logger.log(Level.WARNING, "Order " + randOrder + " has not been approved!");
				}
			}
		}
	}

	/**
	 * Method to write an Order to a text file
	 * @param order an Order that is going to be stored
	 * @return boolean true if operation is successful
	 */
	public static boolean writeOrderAsText(Order order) {

		boolean successful = false;
		String filename = Configuration.getFileNameOrdersReport(); 
		logger.fine("Storing "+ order + " in " + filename);
		try{
			File orderFile = new File(filename);

			if(!orderFile.exists()) {
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename));
				pw.newLine();
				pw.write(order.toString());
				pw.close();
				logger.fine(order.toString() + " has been stored");
			} else{	// If file exist, we add onto it
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename,true)); // A writer that adds the data
				pw.newLine();
				pw.write(order.toString());
				pw.close();
				logger.fine(order.toString() + " has been stored");
			}
			successful = true;
		} catch(IOException ioe) {
			logger.log(Level.SEVERE, "Exception occured while storing order");
		}

		return successful;
	}

	/**
	 * Helper method for handling an Order
	 * @param site a Site defining the Site that handles the Order
	 * @param order an Order that is going to be handled by the Site
	 * @return boolean true if Order is approved
	 */
	private static boolean handleOrder(Order order, Site site) { 
		boolean orderApproved = false;

		try {
			switch(order.getTransactionMode().toString()){
			case "SELL":
				orderApproved = site.sellMoney(order);
				break;
			case "BUY":
				orderApproved = site.buyMoney(order);
				break;
			default:
				break;
			}
		}
		catch(IllegalArgumentException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return orderApproved;
	}

	/**
	 * Helper method for presenting Site menu to CLI and handling input
	 * @param site the Site
	 */
	private static void presentSiteMenu(Site site) {
		boolean exitSiteMenu = false;
		int siteMenuMin = 0, siteMenuMax = 2;
		do {
			System.out.format("%n*** Money Service Site Menu --------------------%n");
			System.out.println("1 - Present current transactions");
			System.out.println("2 - Create site report");
			System.out.println("0 - Exit application");

			int siteUserMenuInput;
			do {
				System.out.format("%nEnter your choice (%d-%d): ", siteMenuMin, siteMenuMax);
				siteUserMenuInput = getInputUint();
				if(siteUserMenuInput> siteMenuMax) {
					System.out.println(siteUserMenuInput + " is not a menu choice!");
				}	
			}while(!(siteUserMenuInput >= siteMenuMin && siteUserMenuInput <= siteMenuMax));

			switch(siteUserMenuInput) {
			case 0:		// Exit to main menu
				exitSiteMenu = true;
				break;
			case 1:		// Present current transactions	
				if(site.getTransactions().isEmpty()) {
					System.out.println("No transactions has been made today!");
				}
				for(Transaction t : site.getTransactions()) {
					System.out.println(t.toString());
				}

				break;
			case 2:		// Create site report (Shutdown)
				// shut down service stores transactions into file
				if(site.getTransactions().isEmpty()) {
					System.out.println("No transactions has been made today!");
				}
				
				site.shutDownService(Configuration.getFileNameTransactionsReport());
				System.out.println("Site report has been created!");
				break;
			}
		}while(!exitSiteMenu);
	}
	
	/**
	 * Helper method for presenting customer menu to CLI and handling input
	 * @param user a User defining the customer
	 * @param site the Site 
	 */
	private static void presentUserMenu(User user, Site site) {
		boolean exitUserMenu = false;
		int userMenuMin = 0, userMenuMax = 1;
		do {
			System.out.format("%n*** Money Service User Menu --------------------%n");
			System.out.println("1 - Create an order");
			System.out.println("0 - Exit application");

			int userMenuInput;
			do {
				System.out.format("%nEnter your choice (%d-%d): ", userMenuMin, userMenuMax);
				userMenuInput = getInputUint();
				if(userMenuInput> userMenuMax) {
					System.out.println(userMenuInput + " is not a menu choice!");
				}	
			}while(!(userMenuInput >= userMenuMin && userMenuInput <= userMenuMax));

			switch(userMenuInput) {
			case 0:		// Exit to main menu
				exitUserMenu = true;
				break;
			case 1:		// Create an order				
				Optional<Order> userOrder = user.userCreatedOrder();
				if(userOrder.isPresent()) {
					Order anOrder = userOrder.get();
					writeOrderAsText(anOrder);
					boolean ok = handleOrder(anOrder, site);
					if(!ok) {
						logger.log(Level.WARNING, userOrder + " could not be approved!");
						System.out.println("Your order has NOT been approved!");
						
					}
					else {
						System.out.println("Your order has been approved!");
					}
				}
				break;
			}
		}while(!exitUserMenu);
	}
}
