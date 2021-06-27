package mongo;

import java.awt.AWTException;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import alerts.WindowsAlert;
import gui.App;
import sites.AmazonScraper;
import sites.BestBuyScraper;

public class CheckStock implements Runnable {
	String uri = "mongodb://localhost:27017";

	MongoClientURI clientURI = new MongoClientURI(uri);
	MongoClient mongoClient = new MongoClient(clientURI);	//Connects to local database
	
	MongoDatabase mongoDatabase = mongoClient.getDatabase("Restock");	//Finds the database called Restock
	MongoCollection<Document> itemsCollection = mongoDatabase.getCollection("Items");	//Finds the collection called Items

	public void startChecking() {
		checkStock();	//Updates every item in the database
		ArrayList<Document> inStock = getItemsInStock();	//Gets all the items that are in stock and stores them in an array list
		sendAlerts(inStock);	//Sends alerts about all items that are in stock
	}	
	
	private ArrayList<Document> getItemsInStock() {
		MongoCursor<Document> cursor = itemsCollection.find().iterator();
		ArrayList<Document> inStock = new ArrayList<Document>();
		
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			if (doc.getBoolean("inStock")) {	//Loops through every item and checks if they are in stock
				inStock.add(doc);	//If they are in stock add to a list that will be returned
			}
		}
		return inStock;
	}
	
	private void sendAlerts(ArrayList<Document> list) {
		InStockLogs logs = new InStockLogs();
		
		WindowsAlert popUp = new WindowsAlert();
		
		for (int i = 0; i < list.size(); i++) {
			try {
				logs.add(list.get(i));	//Adds a new entry to the logs for every item in stock
				popUp.alert(list.get(i));	//Sends a windows notification for every item in stock
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	private void checkStock() {
		MongoCursor<Document> cursor = itemsCollection.find().iterator();
		
		while(cursor.hasNext()) {	//Loops through every item in the database and updates their fields
			Document doc = cursor.next();
			if (doc.getString("website").equals("amazon")) {
				AmazonScraper amz = new AmazonScraper(doc.getString("item"), doc.getString("link"));
				amz.uploadProducts();
			}
			if (doc.getString("website").equals("bestbuy")) {
				BestBuyScraper bb = new BestBuyScraper(doc.getString("item"), doc.getString("link"));
				bb.uploadProducts();
			}
		}
	}
	
	@Override
	public void run() {
		while (App.running) {
			try {
				startChecking();
				Thread.sleep(30000);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}


























