package moneyservice.site.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	
		User user = createUser();
	
		// Hardcoded days and number of orders for now discussion how it should be handled at later stage	
		multipleOrder(user,25);
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
		Site site = new Site("South");
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
