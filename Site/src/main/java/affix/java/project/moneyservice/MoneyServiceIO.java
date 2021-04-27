package affix.java.project.moneyservice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoneyServiceIO {
	
	/* Method that stores a List of Transaction objects in a serializable file, with a designated filename.
	 * @param filename - the name of the file, transactionList - a list of Transaction objects.
	 * @return boolean - true if the List has been stored in the file,
	 *  false if an exception occurs during the process of storing.
	 */
	
	static boolean storeTransactionsAsSer(String filename, List<Transaction> transactionList) {
		String acceptableFile = "ser";
		
		String[] filenameParts = filename.split("\\.");
		if(filenameParts.length == 4 && filenameParts[3].equals(acceptableFile)) {
			try(ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(filename))){
				oos.writeObject(transactionList);
			}catch(IOException ioe) {
				// TODO - Log Error Message
				System.out.println("Exception Occured while storing Objects"+ ioe);
				return false;
			}
			 return true; 
		}
		 return false;
	}
	
	/* Method that de-serializes a file containing Transaction objects.
	 * @param filename - the file to be de-serialized.
	 * @return A list of Transactions.
	 */
	
	@SuppressWarnings("unchecked")
	public static List<Transaction> readReportAsSer(String filename) {
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		String acceptableFile = "ser";
		
		String[] filenameParts = filename.split("\\.");
		if(filenameParts.length == 4 && filenameParts[3].equals(acceptableFile)) {
			try(ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(filename))){
				transactions = (List<Transaction>)ois.readObject();
			}catch(IOException | ClassNotFoundException ioe) {
				//TODO - Log Error MESSAGE
				System.out.println("Exception Occrured while reading Objects"+ ioe);
			}
		}
		  
		return transactions;
	}
	/**
	 * Method to store the Box of Cash from the Site into a text file
	 * @param filename - then name of the file including the path
	 * @param boxOfCash - the map with the box of cash 
	 * @return boolean true if it was stored as a text file
	 */
	static boolean storeBoxOfCashAsText(String filename, Map<String, Double> boxOfCash) {
		boolean stored = false;
		String[] parts = filename.split("\\.");
		if(parts.length == 4 && parts[3].equals("txt")) {
			try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
				pw.println("CurrencyCode Value");
				for(String key : boxOfCash.keySet()) {
					double amount = boxOfCash.get(key);
					pw.println(key + " = " + (int)amount);
				}
			}
			catch(IOException ioe) {
				System.out.println(ioe.getMessage());
			}			
		}
		return stored;
		
	}
}