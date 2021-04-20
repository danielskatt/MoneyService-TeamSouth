package moneyservice.site.app;

import java.util.ArrayList;
import java.util.List;

import moneyservice.model.Configuration;
import moneyservice.model.Order;
import moneyservice.model.Site;
import moneyservice.model.User;

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
	
		
	// Hardcoded days and number of orders for now discussion how it should be handled at later stage	
	multipleOrder(user, 20,25);
		
	}
	/**
	 *  Helper method to create multiple orders per day
	 * @param user
	 * @param numberOfDays
	 * @param numberOfOrders
	 */
	
	public static void multipleOrder(User user, int numberOfDays, int numberOfOrders) {
		
		List<Order> orderList = new ArrayList<Order>();
		
		for(int i=0;i<numberOfDays;i++) {
			for(int k=0;k<numberOfOrders;k++) {
				Order order = createOrder(user);
				if(!(order.equals(null)))
					orderList.add(order);
			}
		}
		
		for(Order temp : orderList) {
			boolean orderApproved = handleOrder(temp);
			
			if(!orderApproved) {
				// TODO: Replace print out with Logging file
				System.out.println("Order not approved: "+temp.toString());
			}
			
			if(orderApproved) {
				// TODO: Replace print out with Logging file
				System.out.println("Order  approved: "+temp.toString());
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
