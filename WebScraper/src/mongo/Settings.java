package mongo;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Settings {
	String uri = "mongodb://localhost:27017";

	MongoClientURI clientURI = new MongoClientURI(uri);
	MongoClient mongoClient = new MongoClient(clientURI);
	
	MongoDatabase mongoDatabase = mongoClient.getDatabase("Restock");	//Connects to the Restock database
	MongoCollection<Document> settingsCollection = mongoDatabase.getCollection("Settings");	//Uses the collection named Settings
	
	
	public void setWindowsAlert(boolean state) {	//Changes the boolean of whether the user wants to receive windows notifications
		if (isItem("windowsAlert", "sendWindowsAlert")) {
			updateItem("windowsAlert", "sendWindowsAlert", state);
		}
		else {
			settingsCollection.insertOne(new Document("setting", "windowsAlert").append("sendWindowsAlert", state));
		}
	}
	
	public void setKeepLogs(boolean state) {	//Changes the boolean of whether the user wants to keep a log of every item that comes in stock
		if (isItem("logs", "keepLogs")) {
			updateItem("logs", "keepLogs", state);
		}
		else {
			settingsCollection.insertOne(new Document("setting", "logs").append("keepLogs", state));
		}
	}
	
	public boolean getWindowsAlertOption() {	//Gets if the user wants to receive windows notifications
		Document doc = (Document) settingsCollection.find(new Document("setting", "windowsAlert")).first();
		if (doc == null) {
			return false;
		}
		return doc.getBoolean("sendWindowsAlert");
	}
	
	public boolean getKeepLogsOption() {	//Gets if the user wants to keep a log of every item that comes in stock
		Document doc = (Document) settingsCollection.find(new Document("setting", "logs")).first();	
		if (doc == null) {
			return false;
		}
		return doc.getBoolean("keepLogs");
	}
	
	private boolean isItem(String settingName, String fieldName) {	
		Document doc = (Document) settingsCollection.find(new Document("setting", settingName)).first();	//Checks if there is a doc in the collection with that name
		if (doc == null) {
			return false;
		}
		return true;
	}
	
	private void updateItem(String settingName, String fieldName, boolean state) {
		Document search = (Document) settingsCollection.find(new Document("setting", settingName)).first();	//Looks for the document to be updated
		Bson updatedBoolean = new Document(fieldName, state);	//Updates the boolean field of that document
		Bson update;
		update = new Document("$set", updatedBoolean);
		settingsCollection.updateOne(search, update);	//Pushes the update to the database
	}
}


