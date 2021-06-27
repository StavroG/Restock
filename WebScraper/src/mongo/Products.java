package mongo;

import data_structures.Product;

import java.util.ArrayList;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;



public class Products {
	String uri = "mongodb://localhost:27017";

	MongoClientURI clientURI = new MongoClientURI(uri);
	MongoClient mongoClient = new MongoClient(clientURI);
	
	MongoDatabase mongoDatabase = mongoClient.getDatabase("Restock");	//Connects to database called Restock
	MongoCollection<Document> itemsCollection = mongoDatabase.getCollection("Items");	//Uses collection called Items
	
	public void uploadProduct(Product product) {
		Document doc = new Document("name", product.title);	//Creates new document to add to items data collection
		doc.append("inStock", product.inStock);	//Adds inStock boolean to the new doc
		doc.append("price", product.price);	//Adds the price to new doc
		doc.append("item", product.item);	//Adds the item name 
		doc.append("link", product.link);	//Adds the link 
		doc.append("website", product.website);	//Adds the web site name
		doc.append("ratings", product.ratings);	//Adds the rating
		
		itemsCollection.insertOne(doc);	//Uploads that new document to the items collection
	}
	
	public void updateProduct(Product product) {	
		Document doc = getDoc(product.link);	//This is the document that will be looked up in the database to be updated
		Bson updatedInStock = new Document("inStock", product.inStock);
		Bson update;
		
		if (product.price != null) {	//If the new price is null then we will not update the price in the database.
			Bson updatedPrice = new Document("price", product.price);
			Bson updatedItem = new Document("item", product.item);
			
			update = new Document("$set", updatedPrice);	//Update the price of the item
			itemsCollection.updateOne(doc, update);
			
			update = new Document("$set", updatedItem);		//Update the item name if users wants to change it
			itemsCollection.updateOne(doc, update);
		}
		
		update = new Document("$set", updatedInStock);	//Update the in stock boolean in the database.
		itemsCollection.updateOne(doc, update); 
	}
	
	public boolean deleteOneProduct(String product) {	//Deletes all the items with the same name as product
		MongoCursor<Document> cursor = itemsCollection.find().iterator();

		boolean hasDeleted = false;
		while(cursor.hasNext()) {
			Document doc = cursor.next();

			if (product.equals(doc.get("item"))) {	//Loops through items collection until finds a document matching the product name
				itemsCollection.deleteOne(doc);	//If that document is found it will be deleted from the collection
				hasDeleted = true;	//If at least one of those items is deleted return true
			}
		}
		return hasDeleted;
	}
	
	public boolean deleteAllProducts() {
		MongoCursor<Document> cursor = itemsCollection.find().iterator();
		
		boolean hasDeleted = false;
		
		while(cursor.hasNext()) {	//Loops through the entire collection and deletes every document in the collection
			itemsCollection.deleteOne(cursor.next());
			hasDeleted = true;
		}
		return hasDeleted;
	}
		
	public ArrayList<Product> getProducts() {	//Returns an array list of every document in the items collection
		MongoCursor<Document> cursor = itemsCollection.find().iterator();
		ArrayList<Product> products = new ArrayList<Product>();
		
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			products.add(new Product(doc.getString("item"), doc.getString("link"), doc.getString("price"),
					doc.getString("website"), doc.getBoolean("inStock"), doc.getString("ratings")));
		}
		return products;
	} 
	
	public Document getDoc(String link) {	//Loops up a document in the items collection by item link
		return (Document) itemsCollection.find(new Document("link", link)).first();
	}
}

