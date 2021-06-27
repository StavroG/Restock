package sites;

import data_structures.Product;
import mongo.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class NewEggScraper implements Scraper{
	Product product;
	
	public NewEggScraper(String item, String link) {
		product = new Product(item, link);	//Creates a new product data type
	}
	@Override
	public boolean isValidWebsite() {
		try {
			//Connects to link
			Document doc = Jsoup.connect(product.link).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
		            .header("cookie", "incap_ses_436_255598=zI1vN7X6+BY84PhGvPsMBjKChVcAAAAAVhJ+1//uCecPhV2QjUMw6w==")
		            .timeout(5000)
		            .get();
			if (doc != null) {
				if ((doc.title().substring(doc.title().lastIndexOf(' ')).trim().equals("Newegg.com"))) {	//Checks if it is a Newegg link
					return true;	
				}
			}
		} catch (Exception e) {
			e.getStackTrace();
		}
		return false;
	}

	@Override
	public void getInfo() {
		try {
			//Connects to link
			Document doc = Jsoup.connect(product.link).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
		            .header("cookie", "incap_ses_436_255598=zI1vN7X6+BY84PhGvPsMBjKChVcAAAAAVhJ+1//uCecPhV2QjUMw6w==")
		            .timeout(0)
		            .get();
			
			product.title = doc.title();	//Gets the title of the item from link
			product.website = "newegg";	//Website is NewEgg
			product.price = doc.getElementsByClass("price-current").text().toString();	//Gets the price of the item
			product.ratings = doc.getElementsByClass("rating-views-info").text();	//Gets the rating of the item
			//Checks if item is in stock
			product.inStock = doc.getElementsByClass("flags-body has-icon-left fa-exclamation-triangle").text().toString().compareTo("OUT OF STOCK") != 0;	
		} catch(Exception e) {
			product.inStock = false;
		}
	}

	@Override
	public void uploadProducts() {
		getInfo();	//Gets items info
		Products db = new Products();	//Creates new instance of a database class
		if (db.getDoc(product.link) == null) {	//If link is not in database then upload new product to database
			db.uploadProduct(product);
		}
		else {
			db.updateProduct(product);	//If link is in the database then update that item
		}
	}

}
