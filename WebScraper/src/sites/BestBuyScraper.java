package sites;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import data_structures.Product;
import mongo.Products;

public class BestBuyScraper implements Scraper{
	Product product;
	
	public BestBuyScraper(String item, String link) {
		product = new Product(item, link);	//Creates a new product data type
	}

	@Override
	public boolean isValidWebsite(){
		try {
			Document doc = Jsoup.connect(product.link).timeout(5000).get();	//Connects to link
			//If the title of the link does have buy then we know it is a valid bestbuy link
			if (doc != null && doc.title().substring(doc.title().length() - 3, doc.title().length()).toLowerCase().equals("buy")) {
				return true;
			}
		} catch (IOException e) {
			return false;
		}
		return false;	//If not a valid bestbuy link return false
	}

	@Override
	public void getInfo() {
		try {
			Document doc = Jsoup.connect(product.link).timeout(5000).get();	//Connects to link

			product.title = doc.title();	//Gets the title of the item
			product.website = "bestbuy";	//Website is bestbuy
			
			//Checks if item is sold out
			if (doc.getElementsByClass("btn btn-disabled btn-lg btn-block add-to-cart-button").toString().contains("Sold Out")) {
				product.inStock = false;
			} else  {
				product.inStock = true;
			}
			
			//Gets the price of the item
			String price = (doc.getElementsByClass("priceView-hero-price priceView-customer-price").text());
			product.price = price.substring(price.lastIndexOf("$"));
			
			//Gets the ratings of the item
			product.ratings = doc.getElementsByClass("overall-rating").text().toString() + " out of 5 stars";
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void uploadProducts() {
		getInfo();	//Gets the item info
		Products db = new Products();	//Creates instance of database class
		
		if (db.getDoc(product.link) == null) {	//If link is not in database then upload new product to database
			db.uploadProduct(product);
		}
		else {
			db.updateProduct(product);	//If link is in database then update the database
		}
	}

}
