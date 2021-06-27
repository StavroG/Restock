package sites;

public interface Scraper {
	public boolean isValidWebsite();	//Makes sure a link is a valid link
	
	public void getInfo();	//Gets the name, price, if in stock and ratings from link
	
	public void uploadProducts();	//Uploads information to the database
}
