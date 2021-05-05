package moneyservice.site.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.XMLFormatter;

import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.Currency;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.Site;
import affix.java.project.moneyservice.Transaction;

/** ----------------_MoneyServiceApp ----------------
 * <p>
 *  Read Configuration file for setting up the configuration 
 *  of the Application. Create User which will create Order(s)
 *  to Site 
 * <p>
 * --------------------------------------------------*/
public class MoneyServiceApp {

//	// TODO - delete
//	/**
//	 * SITE_NAME a String holding the name of the site
//	 */
//	private static final String SITE_NAME = "South";

//	/**
//	 * site a Site object (singleton) that handles orders, transactions and creating reports
//	 */
	static Site site;
	
	/**
	 * logger a Logger 
	 */
	private static Logger logger;
	
	/**
	 * fh a FileHandler
	 */
	private static FileHandler fh;

	public static void main(String[] args) {
		
		logger = Logger.getLogger("affix.java.project.moneyservice");
		
		/*--- Set up configuration ------------------------------------------------*/

		if(args.length > 1) {	// Use argument as file name input to set up configuration (file name format = Configs/<filename>.txt)
			boolean ok = Configuration.parseConfigFile("Configs/" + args[0]); // TODO: refactor with attribute from configuration
			
			if(!ok) {
				System.out.println("ERROR occured when trying to read configuration file and set up configuration!");
				System.out.println("Shutting down program");
				
				logger.info("ERROR occured when trying to read configuration file and set up configuration!");
				System.exit(1);
			}
			
			logger.info(args[0] + " read in as a program argument");
	
		}
		else {	// if no argument for file name is supplied
			boolean ok = Configuration.parseConfigFile("Configs/ProjectConfig_2021-04-01.txt"); // TODO: refactor with attribute from configuration
			
			if(!ok) {
				System.out.println("ERROR occured when trying to read configuration file and set up configuration!");
				System.out.println("Shutting down program");
				
				logger.info("ERROR occured when trying to read configuration file and set up configuration!");
				System.exit(1);
			}

			logger.info("Configs/ProjectConfig_2021-04-01.txt set as default config"); // TODO: refactor

		}


		/*--- Set up log format ---------------------------------------------------*/

		try {	
			// choose formatter for logging output text/xml
			if(Configuration.getLogFormat().equals("text")){
				fh = new FileHandler("MoneyServiceLog.txt");
				fh.setFormatter(new SimpleFormatter());
			}
			else{
				fh = new FileHandler("MoneyServiceLog.xml");
				fh.setFormatter(new XMLFormatter());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.addHandler(fh);
		logger.setLevel(Configuration.getLogLevel());
		
		/*--- Create folder to store transactions ---------------------------------*/

		//TODO - Refactor?
		String siteName = Configuration.getSiteName();		// TODO: does format need to change, currently upper case?
		String directory = "Transactions/";			// TODO: delete?
		File path = new File(directory+siteName);	// TODO: create attribute in Configuration class and refactor this line
		path.mkdir();	// creates a directory if it does not exist
		String [] filesInFolder = path.list();	// get all files in the directory, to get last transaction id
		
		/*--- Create User object --------------------------------------------------*/
		User user = createUser();


		/*--- Set up site ---------------------------------------------------------*/ 
		Map<String, Double> boxOfCash = Configuration.getBoxOfCash();
		Map<String, Currency> currencies = Configuration.getCurrencies();
//		Site site;

		try {
			site = new Site(siteName, boxOfCash, currencies);		// TODO: refactor, siteName is upper case?

			// TODO: delete dir param
			setLastTransactionId(filesInFolder, directory, siteName);

			String newFileName = directory + siteName + File.separator + "Report_" + siteName + "_" + Configuration.getCURRENT_DATE().toString() + ".ser";
			logger.fine("Creating orders!");

			int choice = siteCLI(); // Calling for user to select automatic or manual order creation

			System.out.println("DEBUG");
			switch(choice) {
			case 1:
				Optional<Order> userOrder = user.userCreatedOrder();
				handleOrder(userOrder.get());
				break;
			case 2:
				//TODO: should we remove 25 and have user select number of orders?
				multipleOrder(user,25);
				break;
			}

			site.shutDownService(newFileName);

			List<Transaction> test2 = MoneyServiceIO.readReportAsSer(newFileName);

			for(Transaction t : test2) {
				System.out.println(t.toString());
			}		
			logger.info("End of program!");

		}
		catch (IllegalArgumentException e){
			// TODO: write error message
		}
		catch(NullPointerException e){
			// TODO: write error message (date error when date does not exist)
		}
	}

	/**
	 *  Method siteCLI will be responsible for selecting manual or automatic order input
	 * @return int - representing the choice of user (1 = manual order 2 = automatic)
	 */
	private static int siteCLI() {
		int choice = 0;
		Scanner sc = new Scanner(System.in);

		// Will loop through until user selected correct input
		while(!((choice==1)||(choice==2))) {
			try {
				System.out.format("\nWhat kind of input do you want? \n 1) Manual order input\n 2) Automatic orders\n Choice: ");
				choice = sc.nextInt();
				if(!((choice==2)||(choice==1)))
					System.out.format("Illegal input, expected either number 1 or 2\n");

			} catch(InputMismatchException e) {
				sc.nextLine();
				System.out.format("Expected either number 1 or 2 as input\n");
			}	
		}

		return choice;
	}

	/**
	 *  Helper method to create multiple orders per day
	 * @param user
	 * @param numberOfOrders
	 */
	public static void multipleOrder(User user, int numberOfOrders) {

		int approvedOrderCounter = 0;

		while(approvedOrderCounter < numberOfOrders) {
			Optional<Order> optionalOrder = createOrder(user);
			if(optionalOrder.isPresent()) {
				Order temp = optionalOrder.get();
				logger.fine(temp + " has been placed");
				printOrder(temp);
				boolean orderApproved = handleOrder(temp);

				if(!orderApproved) {
					logger.fine("Order " + temp + " has not been approved!");
				}
				else {
					approvedOrderCounter++;
				}
			}	
		}
	}

	/**
	 * Method to print down order
	 * @param order
	 * @return boolean 
	 */
	public static boolean printOrder(Order order) {

		boolean successful = false;
		String directory = "Orders"+File.separator;
		String filename = directory+"Orders_"+Configuration.getCURRENT_DATE().toString()+".txt";
		logger.fine("Storing "+ order + " in " + filename);
		try{
			File orderFile = new File(filename);

			if(!orderFile.exists()) {
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename));
				pw.write(order.toString());
				pw.newLine();
				pw.close();
				logger.fine(order.toString() + " has been stored");
			} else{	// If file exist, we add onto it
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename,true)); // A writer that adds the data
				pw.write(order.toString());
				pw.newLine();
				pw.close();
				logger.fine(order.toString() + " has been stored");
			}
			successful = true;
		} catch(IOException ioe) {
			logger.log(Level.WARNING, "Exception occured while storing order");
			System.out.println("Exception occured while storing order: "+ ioe);
		}


		return successful;
	}

	// TODO: delete
	/**
	 * Helper method for creating a User
	 * @return - Created User
	 */
	private static User createUser() {
		User user = new User("User 1");
		logger.fine("User " + user.getName() + " created!");
		return user;
	}

	// TODO: delete
	/**
	 * 	Helper method to create an order
	 * @param user
	 * @return Optional<Order>
	 */
	private static Optional<Order> createOrder(User user){	

		Optional<Order> optionalOrder = user.createOrderRequest();

		return optionalOrder;
	}

	/**
	 * Helper method for handling an Order
	 * @param site
	 * @param order
	 * @return boolean - true for approved, false for not approved.
	 */
	private static boolean handleOrder(Order order) { 
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
			System.out.println(e.getMessage());
		}

		return orderApproved;
	}

	/**
	 * Helper method that provides the program with the latest Transactions id number.
	 * Used for updating the unique id count.
	 * @param filesInFolder
	 * @param directory
	 * @param siteName
	 */
	private static void setLastTransactionId(String [] filesInFolder, String directory, String siteName) {

		if(filesInFolder.length > 0) {
			//Gets the last file in the folder.
			String lastFile = filesInFolder[filesInFolder.length - 1];

			// Creats a new filename for the read in last file.
			String lastFileName = directory + siteName + File.separator + lastFile;

			//Reads the contents of the last ser file found in the folder and provides a list of Transactions.
			List<Transaction> lastDayTransactions = MoneyServiceIO.readReportAsSer(lastFileName);

			//If not empty, gets the id number of the latest Transaction, increments and sets the uniqueId as a new counter.
			if(!lastDayTransactions.isEmpty()) {
				Transaction lastTransaction = lastDayTransactions.get(lastDayTransactions.size() - 1);
				int lastId = lastTransaction.getId() + 1;
				lastTransaction.setId(lastId);
			}
		}

	}

}
