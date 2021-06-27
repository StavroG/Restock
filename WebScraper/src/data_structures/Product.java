package data_structures;

public class Product {
	public String title;
	public String price;
	public String item;
	public String link;
	public String ratings;
	public String website;
	public String time;
	
	public boolean inStock;
	
	public Product(String item, String link) {	//Used in the sites classes
		this.item = item;
		this.link = link;
	}
	
	public Product(String item, String link, String price, String website, boolean inStock, String ratings) { //Used in the database classes
		this.item = item;
		this.link = link;
		this.price = price;
		this.inStock = inStock;
		this.website = website;
		this.ratings = ratings;
	}
	
	public Product(String item, String price, String website, String time) {	//Used in the database classes
		this.item = item;
		this.price = price;
		this.website = website;
		this.time = time;
	}
	
	
	//Getters for values of the product
	public String getTile() {
		return title;
	}
	
	public String getPrice() {
		return price;
	}
	
	public String getItem() {
		return item;
	}
	public String getLink() {
		return link;
	}
	
	public String getWebsite() {
		return website;
	}
	
	public boolean getInStock() {
		return inStock;
	}
	
	public String getRatings() {
		return ratings;
	}
	
	public String getTime() {
		return time;
	}
}
