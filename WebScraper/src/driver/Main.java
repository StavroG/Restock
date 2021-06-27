/*
 * @author: Stavro Gorou
 * GitHub: https://github.com/StavroG
 * Date (MM/DD/YY):	06/10/21 
 * Description: An app that will allow the user to create a wish list. Once items in the wish list are in stock the user receives a notification. 
 * Web sites supported are Amazon BestBuy and NewEgg
 */

package driver;

import gui.App;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
	public static void main(String[] args) {
		launch(args);
	}		
	
	@Override
	public void start(Stage arg0) throws Exception {
		new App();
	}
}








