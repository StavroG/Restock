package sites;

import data_structures.Product;
import mongo.Products;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class AmazonScraper implements Scraper{
	Product product;
	
	public AmazonScraper(String item,String link) {
		product = new Product(item, link);	//Creates a new product data type
	}
	
	@Override
	public void getInfo() {
		try {
			//Connect to the link over Internet browser
			Document doc = Jsoup.connect(product.link).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
		            .header("cookie", "incap_ses_436_255598=zI1vN7X6+BY84PhGvPsMBjKChVcAAAAAVhJ+1//uCecPhV2QjUMw6w==")
		            .timeout(5000)
		            .get();
			
			product.title = doc.title();	//Gets the title of the item
			product.website = "amazon";	//Name of web site
			product.price = doc.getElementsByClass("a-offscreen").text().toString();	//Gets the price of the item
			product.price = product.price.substring(0, product.price.indexOf(" "));	//Cleans up the price to just give us the numbers
			product.ratings = doc.getElementById("acrPopover").text();	//Gets the ratings of an item

			//Check to see if item is in stock by looking for an element saying currently unavailable
			if (doc.getElementsByClass("a-color-price a-text-bold").text().equals("Currently unavailable.")) {
				product.inStock = false;
			}
			else {
				product.inStock = true;
			}
		} catch (Exception e) {	//If can not find an element saying currently unavailable, that means the product is in stock
			product.inStock = true;
			e.printStackTrace();
		}
	}

	@Override
	public void uploadProducts() {
		getInfo();	//Gets all the info we need for the product
		Products db = new Products();	//Instantiate database class
		if (db.getDoc(product.link) == null) {	//Checks to make sure item is not already in database
			db.uploadProduct(product);	//If not then add it to database
		}
		else {
			db.updateProduct(product);	//If item is in database than update its values
		}
	}

	@Override
	public boolean isValidWebsite(){
		try {
			//Connects to link
			Document doc = Jsoup.connect(product.link).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
		            .header("cookie", "incap_ses_436_255598=zI1vN7X6+BY84PhGvPsMBjKChVcAAAAAVhJ+1//uCecPhV2QjUMw6w==")
		            .timeout(5000)
		            .get();
			//If the title of the web site says amazon then it is a valid amazon link
			if (doc != null && doc.title().substring(0, 6).toLowerCase().equals("amazon")) {	
				return true;
			}
		} catch (Exception e) {
			return false;
		}
		return false;	//If title does not say amazon then it is not a valid amazon link
	}
}