package moneyservice.java.model;

public class Order {
	
	// ATTRIBUTES
	// TODO: attributes should be final?
	
	private final String site;	// SOUTH or south or South
	private final String currencyCode;
	private final int amount;
	private final TransactionMode mode;	// TODO: change name to transactionMode? more readable
	
	// DEFAULT CONSTRUCTOR
	public Order(String site, String currencyCode, int amount, Transaction mode) {
		this.site = site;
		this.currencyCode = currencyCode;	// NB! needs to be checked or have a certain input from User
		this.amount = amount;
		this.mode = mode;
	}

	// METHODS
	// TODO: create methods for hashCode, compareTo and equals?
	
	// GETTERS AND SETTERS
	
	public String getSite() {
		return site;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public int getAmount() {
		return amount;
	}

	public TransactionMode getMode() {
		return mode;
	}

	// TOSTRING
	// TODO: refactor
	@Override
	public String toString() {
		return String.format("Order [site=%s, currencyCode=%s, amount=%s]", site, currencyCode, amount);
	}

}
