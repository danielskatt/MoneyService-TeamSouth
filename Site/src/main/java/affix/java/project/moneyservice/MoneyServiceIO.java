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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
/**
 * This class acts as a bridge between Money Service Site and Money Service HQ. 
 * It handles input and output files related to Money Service application.
 * It is used to serialize and de-serialize Site reports containing daily transactions.
 * It is used to store the Box of Cash from Site in a text file.
 * NB! To store files in a specific directory the file name needs to include the path
 * to that directory.
 */
public class MoneyServiceIO {
	
	/**
	 * logger a Logger
	 */
	private static Logger logger;
	
	/**
	 * Setter for attribute logger
	 */
	static{logger = Logger.getLogger("affix.java.project.moneyservice");}

	
	/**
	 * This method stores a List of Transaction objects in a serializable file, with a designated filename.
	 * @param fileName a String holding the file name including the path to store transactions to
	 * @param transactionList a {@code List<Transaction>} holding Transaction objects to be stored
	 * @return boolean true if operation was successful 
	 */
	static boolean storeTransactionsAsSer(String fileName, List<Transaction> transactionList) {
		String acceptableFile = ".ser";
		String extension = "";
		try {
			extension = fileName.substring(fileName.lastIndexOf("."));
		} catch(IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE,"Exception at splitting filename");
		}
		if(extension.equals(acceptableFile)) {
			try(ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(fileName))){
				oos.writeObject(transactionList);
			}catch(IOException ioe) {
				logger.log(Level.SEVERE, "Exception occured while storing to file");
				return false;
			}
			 return true; 
		}
		 return false;
	}
	
	/**
	 * This method de-serializes a .ser file containing Transaction objects
	 * @param fileName a String holding he file name including the path to de-serialize
	 * @return transactionList a {@code List<Transaction>} holding the Transaction objects in the file
	 */
	@SuppressWarnings("unchecked")
	public static List<Transaction> readReportAsSer(String fileName) {
		List<Transaction> transactions = new ArrayList<Transaction>();
		String acceptableFile = ".ser";
		String extension = "";

		try {
			extension = fileName.substring(fileName.lastIndexOf("."));
		} catch(IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE,"Exception at splitting filename");
		}
		if(extension.equals(acceptableFile)) {
			try(ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(fileName))){
				transactions = (List<Transaction>)ois.readObject();
			}catch(IOException | ClassNotFoundException ioe) {
				logger.log(Level.SEVERE, "Exception occured while reading from file");
			}
		}
		  
		return transactions;
	}
	
	/**
	 * This method is used to store the attribute Box of Cash from class Site into a text file
	 * @param fileName a String holding the file name including the path
	 * @param boxOfCash a {@code Map<String, Double>} containing code of the currency and the associated amount 
	 * @return boolean true if operation was successful 
	 */
	static boolean storeBoxOfCashAsText(String fileName, Map<String, Double> boxOfCash) {
		boolean stored = false;
		String acceptableFile = ".txt";
		String extension = "";

		try {
			extension = fileName.substring(fileName.lastIndexOf("."));
		} catch(IndexOutOfBoundsException e) {
			logger.log(Level.SEVERE,"Exception at splitting filename");
		}
		if(extension.equals(acceptableFile)) {
			try(PrintWriter pw = new PrintWriter(new FileWriter(fileName))) {
				pw.println("CurrencyCode Value");
				for(String key : boxOfCash.keySet()) {
					double amount = boxOfCash.get(key);
					pw.println(key + " = " + (int)amount);
				}
				stored = true;
			}
			catch(IOException ioe) {
				logger.log(Level.SEVERE, "Error occured while storing boxofCash!");
			}			
		}
		
		return stored;
	}
}