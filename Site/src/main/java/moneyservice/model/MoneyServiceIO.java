package moneyservice.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MoneyServiceIO {
	
	/* Method that stores a List of Transaction objects in a serializable file, with a designated filename.
	 * @param filename - the name of the file, transactionList - a list of Transaction objects.
	 * @return boolean - true if the List has been stored in the file,
	 *  false if an exception occurs during the process of storing.
	 */
	static boolean storeTransactionsAsSer(String filename, List<Transaction> transactionList) {
		 
		try(ObjectOutputStream oos = new ObjectOutputStream(
				  new FileOutputStream(filename))){
			  oos.writeObject(transactionList);
		  }catch(IOException ioe) {
			  // TODO - Log Error Message
			  System.out.println("Exception Occured "+ ioe);
			  return false;
		  }
		  return true; 
		
	}
	
	/* Method that de-serializes a file containing Transaction objects.
	 * @param filename - the file to be de-serialized.
	 * @return A list of Transactions.
	 */
	
	@SuppressWarnings("unchecked")
	static List<Transaction> readReportAsSer(String filename) {
		
		List<Transaction> transactions = new ArrayList<Transaction>();
		
		  try(ObjectInputStream ois = new ObjectInputStream(
				  new FileInputStream(filename))){
			  transactions = (List<Transaction>)ois.readObject();
			  return transactions;
		  }catch(IOException | ClassNotFoundException ioe) {
			  //TODO - Log Error MESSAGE
			  System.out.println("Exception Occrured while reading Objects"+ ioe);
		  }
		return transactions;
	}
}
