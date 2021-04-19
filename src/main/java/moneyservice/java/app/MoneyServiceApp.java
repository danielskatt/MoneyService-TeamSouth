package moneyservice.java.app;

import moneyservice.java.model.Configuration;
import moneyservice.java.model.Order;
import moneyservice.java.model.Site;
import moneyservice.java.model.User;

/** ----------------_MoneyServiceApp ----------------
 * <p>
 *  Read Configuration file for setting up the configuration 
 *  of the Application. Create User which will create Order(s)
 *  to Site 
 * <p>
 * --------------------------------------------------*/
public class MoneyServiceApp {

	public static void main(String[] args) {
		if(args.length > 0) {
			Configuration.parseConfigFile(args[0]);
		}
		else {
			Configuration.parseConfigFile("ProjectConfig_2021-04-01.txt");
		}
		
		Site theSite = new Site("South");
		
		User user = createUser();
		Order order = createOrder(user);
		boolean store = handleOrder(order);
		
		if(store) {
			theSite.storeTransaction(order);
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
	 * Helper method for creating an Order
	 * @param user
	 * @return
	 */
	private static Order createOrder(User user) {
		Order order = user.createOrderRequest();
		
		return order;
	}
	
	/**
	 * Helper method for handling an Order
	 * @param site
	 * @param order
	 * @return
	 */
	private static boolean handleOrder(Order order) {
		Site site = new Site("Temp");
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
