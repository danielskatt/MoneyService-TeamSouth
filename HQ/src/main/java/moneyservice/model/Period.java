package moneyservice.model;

/**
 * 
 * @author danielskatt
 *
 */
public enum Period {
	NONE("None", 0),
	DAY("Day", 1),
	WEEK("Week", 2),
	YEAR("Year", 3);
	
	/**
	 * 
	 */
	private final String name;
	/**
	 * 
	 */
	private final int numVal;
	
	/**
	 * 
	 * @param name
	 * @param numVal
	 */
	Period(String name, int numVal) {
		this.name = name;
		this.numVal = numVal;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the numVal
	 */
	public int getNumVal() {
		return numVal;
	}
	
	
	
}


