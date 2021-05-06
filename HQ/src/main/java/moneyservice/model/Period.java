package moneyservice.model;

/**
 * An enumeration holding values for the available periods to choose when generating 
 * statistics
 */
public enum Period {
	NONE("None", 0),
	DAY("Day", 1),
	WEEK("Week", 2),
	MONTH("Month", 3);
	
	/**
	 * name a String defining the name for the enumeration value
	 */
	private final String name;
	
	/**
	 * numVal an int holding a number for the enumeration value
	 */
	private final int numVal;
	
	/**
	 * Default constructor for enumeration
	 * @param name a String defining the name for the enumeration value
	 * @param numVal an int holding a number for the enumeration value
	 */
	Period(String name, int numVal) {
		this.name = name;
		this.numVal = numVal;
	}

	/**
	 * Getter for attribute name
	 * @return name a String defining the name for the enumeration value
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter for attribute numVal
	 * @return numVal an int holding a number for the enumeration value
	 */
	public int getNumVal() {
		return numVal;
	}
}


