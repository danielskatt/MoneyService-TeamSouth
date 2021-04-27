package moneyservice.model;


/**
 * 	Enumeration for all sites supported by HQ.
 * 	Contains the corresponding value for CLI choices in HQ
 */
public enum MoneyServiceSites {
	NONE("None", 0),
	NORTH("North", 1),
	EAST("East", 2),
	CENTER("Center", 3),
	SOUTH("South", 4),
	ALL("All", 5);
	
	/**
	 * @attribute name a String defining name of Site
	 */
	private final String name;
	/**
	 * @attribute numVal an int defining the numerical value
	 */
	private final int numVal;
	
	
	/**
	 * Default constructor
	 * @param name
	 * @param numVal
	 */
	MoneyServiceSites(String name, int numVal) {
		this.name = name;
		this.numVal = numVal;
	}
	
	/**
	 * Getter for String attribute name
	 * @return name a String defining name of Site
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for int attribute numVal
	 * @return numVal an int defining the numerical value
	 */
	public int getNumVal() {
		return numVal;
	}
}