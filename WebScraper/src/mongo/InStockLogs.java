package mongo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import data_structures.Product;

public class InStockLogs {
	String uri = "mongodb://localhost:27017";

	MongoClientURI clientURI = new MongoClientURI(uri);
	MongoClient mongoClient = new MongoClient(clientURI);
	
	MongoDatabase mongoDatabase = mongoClient.getDatabase("Restock");	//Uses database called Restock	
	MongoCollection<Document> logsCollection = mongoDatabase.getCollection("StockLogs");	//Uses collection called StockLogs
	
	public void add(Document doc) {
		Document addDoc = new Document("item", doc.getString("item"));	//Creates a new document to be added to the logs 
		addDoc.append("price", doc.getString("price"));	//Adds price to the new log entry
		addDoc.append("website", doc.getString("website"));	//Adds website to the new log entry
		addDoc.append("time", getTime());	//Adds the time to the new log entry
		logsCollection.insertOne(addDoc);	//Uploads the new entry to the log collection in the database
	}
	
	private String getTime() {
		DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
		return(time.format(LocalDateTime.now()));	//Gets current time 
	}
	
	public ArrayList<Product> getLogs() {
		MongoCursor<Document> cursor = logsCollection.find().iterator();
		ArrayList<Product> logs = new ArrayList<Product>();
		
		while(cursor.hasNext()) {	//Loops through the logs and gets returns every item in an array list
			Document doc = cursor.next();
			logs.add(new Product(doc.getString("item"), doc.getString("price"), doc.getString("website"), doc.getString("time")));
		}
		return logs;
	}
	
	public void clearLogs() {
		MongoCursor<Document> cursor = logsCollection.find().iterator();

		while(cursor.hasNext()) {	//Loops through every item in the logs and deletes them
			logsCollection.deleteOne(cursor.next());
		}
	}
}
