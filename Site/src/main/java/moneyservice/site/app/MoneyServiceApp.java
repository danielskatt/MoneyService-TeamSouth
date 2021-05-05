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

	// TODO - Add this from configuration
	private static final String SITE_NAME = "South";

	static Site site;
	private static Logger logger;
	private static FileHandler fh;

	public static void main(String[] args) {
		String logFormat = "text";
		Level currentLevel = Level.ALL;
		logger = Logger.getLogger("affix.java.project.moneyservice");
		List<String> configParams = null;

		if(args.length > 1) {
			boolean ok = Configuration.parseConfigFile("Configs/" + args[0]);
			if(!ok) {
				logger.info("An error occured while reading and setting Config params!");
				System.exit(1);
			}
			logger.info(args[0] + " read in as a program argument");
			configParams = parseLogConfig(args[1]);
			logger.info(args[1] + " read in as a program argument");	
		}
		else {
			boolean ok = Configuration.parseConfigFile("Configs/ProjectConfig_2021-04-01.txt");
			if(!ok) {
				logger.info("An error occured while reading and setting Config params!");
				System.exit(1);
			}

			logger.info("Configs/ProjectConfig_2021-04-01.txt set as default config");

			configParams = parseLogConfig("LogConfig.txt");
			logger.info("LogConfig.txt set as default log config");
		}

		logFormat = configParams.get(0);
		logger.info(logFormat + " is set as a current logformat");
		String level = configParams.get(1);
		currentLevel = Level.parse(level);
		logger.info(currentLevel + " is set as the current level of log filtering");


		try {    
			// choose formatter for logging output text/xml
			if(logFormat.equals("text")){
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
		logger.setLevel(currentLevel);
		// Create folder in Project HQ to store report
		//TODO - Delete this and add from configuration
		String siteName = "SOUTH";
		String directory = "Transactions/";
		File path = new File(directory+siteName);
		path.mkdir();
		String [] filesInFolder = path.list();
		User user = createUser();


		// Set up site
		Map<String, Double> boxOfCash = Configuration.getBoxOfCash();
		Map<String, Currency> currencies = Configuration.getCurrencies();

		try {
			site = new Site(SITE_NAME, boxOfCash, currencies);

			// Make this a method params: String [] filesInFolder, return void
			setLastTransactionId(filesInFolder, directory, siteName);

			String newFileName = directory + siteName + File.separator + "Report_" + siteName + "_" + Configuration.getCURRENT_DATE().toString() + ".ser";
			logger.fine("Creating orders!");

			int choice = siteCLI(); // Calling for user to select automatic or manual order creation

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
			logger.log(Level.SEVERE, e.getMessage());
		}
		catch(NullPointerException e){
			// TODO: write error message (date error when date does not exist)
			logger.log(Level.SEVERE, e.getMessage());
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
				//logger.fine(choice + " choosen as input");
				if(!((choice==2)||(choice==1))) {
					logger.log(Level.WARNING, choice + " is not valid choice");
					System.out.format("Illegal input, expected either number 1 or 2\n");
				}
			} catch(InputMismatchException e) {
				sc.nextLine();
				logger.log(Level.WARNING, e.getMessage());
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
					logger.log(Level.SEVERE,"Order " + temp + " has not been approved!");
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
			logger.log(Level.SEVERE, "Exception occured while storing order");
			System.out.println("Exception occured while storing order: "+ ioe);
		}


		return successful;
	}

	/**
	 * Helper method for creating a User
	 * @return - Created User
	 */
	private static User createUser() {
		User user = new User("User 1");
		logger.fine("User " + user.getName() + " created!");
		return user;
	}

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
			logger.log(Level.SEVERE, e.getMessage());
		}

		return orderApproved;
	}

	/**
	 * Helper method for parsing the LogConfig file.
	 * @param logConfig
	 * @return List with the config parameters such as, logformat and loglevel.
	 */
	private static List<String> parseLogConfig(String logConfig) {
		File configFile = new File(logConfig);
		logger.fine("Parsing " + logConfig);
		List<String> configParams = new ArrayList<>();

		try(BufferedReader br = new BufferedReader(new FileReader(configFile))){
			while(br.ready()){
				String configString = br.readLine();			
				String [] configParts = configString.split("=");

				if(configParts.length == 2) {
					String key = configParts[0].strip();
					String value = configParts[1].strip();

					switch(key) {
					case "Logformat":
						switch(value) {
						case "text":
							configParams.add(value);
							break;
						case "xml":
							configParams.add(value);
							break;
						default:
							logger.log(Level.WARNING, value + " invalid as logformat ");
							break;
						}

						break;
					case "Loglevel":
						switch(value) {
						case "INFO":
							//currentLevel = Level.parse(value);
							configParams.add(value);
							break;
						case "ALL":
							//currentLevel = Level.parse(value);
							configParams.add(value);
							break;
						case "WARNING":
							//currentLevel = Level.parse(value);
							configParams.add(value);
							break;
						case "FINE":
							configParams.add(value);
							break;
						case "FINER":
							configParams.add(value);
							break;
						case "FINEST":
							configParams.add(value);
							break;
						default:
							logger.log(Level.WARNING, value + " invalid loglevel ");
							break;
						}
						break;
					default:
						logger.log(Level.WARNING, key + " invalid parameter!");

					}

				}
			}
		} 
		catch (IOException ioe) {
			logger.log(Level.WARNING, "An exception occured while reading LogConfig");
			System.out.println("Exception occurred: " + ioe);
		}
		return configParams;
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
