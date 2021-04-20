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
		Order order = createOrder(user);
		boolean store = handleOrder(order);
		
		int numberOfOrders = 25;
		int numberOfDays = 20;
		
		List<Order> orderList = new ArrayList<Order>();
		
		for(int i=0;i<numberOfDays;i++) {
			for(int k=0;k<numberOfOrders;k++) {
				Order order1 = createOrder(user);
				if(!(order1.equals(null)))
					orderList.add(order1);
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
				System.out.println("Order not approved: "+temp.toString());
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
