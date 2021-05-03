package moneyservice.site.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import affix.java.project.moneyservice.User;

/** ----------------_MoneyServiceApp ----------------
 * <p>
 *  Read Configuration file for setting up the configuration 
 *  of the Application. Create User which will create Order(s)
 *  to Site 
 * <p>
 * --------------------------------------------------*/
public class MoneyServiceApp {

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
			configParams = parseLogConfig(args[1]);
			logFormat = configParams.get(0);
			String level = configParams.get(1);
			currentLevel = Level.parse(level);
		}
		else {
			boolean ok = Configuration.parseConfigFile("Configs/ProjectConfig_2021-04-01.txt");
			if(!ok) {
				logger.info("An error occured while reading and setting Config params!");
				System.exit(1);
			}
			configParams = parseLogConfig("LogConfig.txt");
			logFormat = configParams.get(0);
			String level = configParams.get(1);
			currentLevel = Level.parse(level);
		}

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
		String siteName = "SOUTH";
		String directory = ".." + File.separator + "HQ" + File.separator + "/Transactions/";
		File path = new File(directory+siteName);
		path.mkdir();
		String [] filesInFolder = path.list();
		User user = createUser();

		// Set up site
		Map<String, Double> boxOfCash = Configuration.getBoxOfCash();
		Map<String, Currency> currencies = Configuration.getCurrencies();


		try {
			site = new Site(SITE_NAME, boxOfCash, currencies);
			
			// DEBUG
			Optional <Double> test = site.getAvailableAmount("SEK");
			if(test.isPresent()) {
				double availableAmount = test.get();
				System.out.println("DEBUG: "+availableAmount);
			}
			
			if(filesInFolder.length > 0) {
				
				String lastFile = filesInFolder[filesInFolder.length - 1];
				String lastFileName = directory + siteName + File.separator + lastFile;
				
				List<Transaction> lastDayTransactions = MoneyServiceIO.readReportAsSer(lastFileName);
				
				if(!lastDayTransactions.isEmpty()) {
					Transaction lastTransaction = lastDayTransactions.get(lastDayTransactions.size() - 1);
					
					int lastId = lastTransaction.getId() + 1;
					
					lastTransaction.setId(lastId);
				}
			}
			
			String newfilename = directory + siteName + File.separator + "Report_" + siteName + "_" + Configuration.getCURRENT_DATE().toString() + ".ser";
			multipleOrder(user,25);
			
			site.shutDownService(newfilename);
			
			List<Transaction> test2 = MoneyServiceIO.readReportAsSer(newfilename);
			
			for(Transaction t : test2) {
				System.out.println(t.toString());
			}		
			logger.info("End of program!");
		}
		catch (IllegalArgumentException e){
			// TODO: write error message
		}
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
				printOrder(temp);
				boolean orderApproved = handleOrder(temp);

				if(!orderApproved) {
					// TODO: Replace print out with Logging file
					logger.fine("Order "+ temp +"has not been approved!");
					//System.out.println("Order not approved: "+temp.toString());
				}
				else {
					approvedOrderCounter++;
					// TODO: Replace print out with Logging file
					//logger.fine("Order "+ temp + "has been approved!");
					//System.out.println("Order approved: "+temp.toString());
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
		String folderName = "Orders";
		String directory = ".." +File.separator +"HQ"+File.separator+folderName+File.separator;
		String filename = directory+"Orders_"+Configuration.getCURRENT_DATE().toString()+".txt";
		try{
			File orderFile = new File(filename);

			if(!orderFile.exists()) {
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename));
				pw.write(order.toString());
				pw.newLine();
				pw.close();
			} else{	// If file exist, we add onto it
				BufferedWriter pw = new BufferedWriter(new FileWriter(filename,true)); // A writer that adds the data
				pw.write(order.toString());
				pw.newLine();
				pw.close();
			}
			successful = true;
		} catch(IOException ioe) {
			//TODO - Log Error MESSAGE
			System.out.println("Exception occrured while saving order: "+ ioe);
		}


		return successful;
	}

	/**
	 * Helper method for creating a User
	 * @return - Created User
	 */
	private static User createUser() {
		User user = new User("User 1");
		logger.fine("User" + user + "created!");
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

		switch(order.getTransactionMode().toString()){
		case "SELL":
			orderApproved = site.buyMoney(order);
			break;
		case "BUY":
			orderApproved = site.sellMoney(order);
			break;
		default:
			break;
		}

		return orderApproved;
	}

	private static List<String> parseLogConfig(String logConfig) {
		File configFile = new File(logConfig);
		System.out.println("LogConfig file read in: "+ configFile.toString());
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
							System.out.println("No such logformat exist");
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
						default:
							System.out.println("No such loglevel exist");
							break;
						}
						break;
					default:
						System.out.println("No key as %s could be found" + key);

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

}
