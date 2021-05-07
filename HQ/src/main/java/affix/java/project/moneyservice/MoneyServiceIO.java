package affix.java.project.moneyservice;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import java.util.TreeMap;

public class MoneyServiceIO {
	
	/* Method that stores a List of Transaction objects in a serializable file, with a designated filename.
	 * @param filename - the name of the file, transactionList - a list of Transaction objects.
	 * @return boolean - true if the List has been stored in the file,
	 *  false if an exception occurs during the process of storing.
	 */
	
	private static Logger logger;
	
	static{
		logger = Logger.getLogger("affix.java.project.moneyservice");
	}
	
	static boolean storeTransactionsAsSer(String filename, List<Transaction> transactionList) {
		String acceptableFile = "ser";
		
		String[] filenameParts = filename.split("\\.");
		if(filenameParts.length == 2 && filenameParts[1].equals(acceptableFile)) {
			try(ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(filename))){
				oos.writeObject(transactionList);
			}catch(IOException ioe) {
				logger.log(Level.SEVERE, "Exception occured while storing to file");
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
		String acceptableFile = ".ser";
		
		String extension = filename.substring(filename.lastIndexOf("."));
		if(extension.equals(acceptableFile)) {
			try(ObjectInputStream ois = new ObjectInputStream(
					new FileInputStream(filename))){
				transactions = (List<Transaction>)ois.readObject();
			}catch(IOException | ClassNotFoundException ioe) {
				logger.log(Level.SEVERE, "Exception occured while reading from file");
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
		if(parts.length == 2 && parts[1].equals("txt")) {
			try(PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
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
	
	/**
	 * This method parses the Site report and returns a Map holding the Currency code and amount
	 * @param filename - the file name of the file that will be parsed
	 * @return a Map holding Currency code as key and amount as value
	 */
	public static Map<String, Double> readSiteReport(String filename){
		Map<String, Double> temp = new TreeMap<String, Double>();
		// logger.info("Reading currency rates from " + filename);
		
		try(BufferedReader br = new BufferedReader(new FileReader(filename))){
			while(br.ready()) {
				String eachLine = br.readLine();
				String parts[] = eachLine.split("=");
				if(parts.length == 2) {
					String currencyCode = parts[0].strip();
					Double value = Double.parseDouble(parts[1].strip());
					
					temp.putIfAbsent(currencyCode, value);
				}
			}
		}
		catch(IOException ioe) {
			 logger.log(Level.SEVERE, ioe.getMessage());
			
		}
		catch(NumberFormatException e) {
			 logger.log(Level.SEVERE, e.getMessage());
		
		}
		catch(DateTimeParseException dte) {
			 logger.log(Level.SEVERE, dte.getMessage());
			
		}
		
		return temp;
	}
}