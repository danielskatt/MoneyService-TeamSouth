package moneyservice.site.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import affix.java.project.moneyservice.Configuration;
import affix.java.project.moneyservice.MoneyServiceIO;
import affix.java.project.moneyservice.Order;
import affix.java.project.moneyservice.Site;
import affix.java.project.moneyservice.Transaction;
import affix.java.project.moneyservice.TransactionMode;
import affix.java.project.moneyservice.User;

/** ----------------_MoneyServiceApp ----------------
 * <p>
 *  Read Configuration file for setting up the configuration 
 *  of the Application. Create User which will create Order(s)
 *  to Site 
 * <p>
 * --------------------------------------------------*/
public class MoneyServiceApp {
	
	static Site site;

	public static void main(String[] args) {
		if(args.length > 0) {
			Configuration.parseConfigFile(args[0]);
		}
		else {
			Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		}
    
		
		// Create folder in Project HQ to store report
		String siteName = "SOUTH";
		String directory = ".." + File.separator + "HQ" + File.separator;
		File path = new File(directory+siteName);
		boolean folderCreated = path.mkdir();
		String [] filesInFolder = path.list();
		User user = createUser();
		site = new Site("South");
		
		if(filesInFolder.length == 0) {
			String filename = directory + siteName + File.separator + "Report_" + siteName + "_" + Configuration.getCURRENT_DATE().toString() + ".ser";
			multipleOrder(user,25);

			site.shutDownService(filename);
			List<Transaction> test = MoneyServiceIO.readReportAsSer(filename);
			for(Transaction t : test) {
				System.out.println(t.toString());
			}
			
		}
		else {
			String lastFile = filesInFolder[filesInFolder.length - 1];
			String filename = directory + siteName + File.separator + lastFile;
			List<Transaction> test = MoneyServiceIO.readReportAsSer(filename);
			
			Transaction lastTransaction = test.get(test.size() - 1);
			
			int lastId = lastTransaction.getId() + 1;
			
			lastTransaction.setId(lastId);
			String newfilename = directory + siteName + File.separator + "Report_" + siteName + "_" + Configuration.getCURRENT_DATE().toString() + ".ser";
			multipleOrder(user,25);
			
			site.shutDownService(newfilename);
			
			List<Transaction> test2 = MoneyServiceIO.readReportAsSer(newfilename);
			
			for(Transaction t : test2) {
				System.out.println(t.toString());
			}
		}
		
		
	}
	
	/**
	 *  Helper method to create multiple orders per day
	 * @param user
	 * @param numberOfDays
	 * @param numberOfOrders
	 */
	public static void multipleOrder(User user, int numberOfOrders) {
		
		//List<Order> orderList = new ArrayList<Order>();
		int approvedOrderCounter = 0;
	
		while(approvedOrderCounter < numberOfOrders) {
			Optional<Order> optionalOrder = createOrder(user);
			if(optionalOrder.isPresent()) {
				//orderList.add(optionalOrder.get());
				Order temp = optionalOrder.get();
				boolean orderApproved = handleOrder(temp);
				
				if(!orderApproved) {
					// TODO: Replace print out with Logging file
					System.out.println("Order not approved: "+temp.toString());
				}
				else {
					approvedOrderCounter++;
					// TODO: Replace print out with Logging file
					System.out.println("Order approved: "+temp.toString());
				}
			}	
		}
	}
	
	/**
	 * Helper method for creating a User
	 * @return - 
	 */
	private static User createUser() {
		User user = new User("User 1");
		
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
	 * @return
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

}
