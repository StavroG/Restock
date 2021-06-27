package gui;

import java.awt.Desktop;
import java.net.URI;

import data_structures.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mongo.CheckStock;
import mongo.InStockLogs;
import mongo.Products;
import mongo.Settings;
import sites.AmazonScraper;
import sites.BestBuyScraper;
import sites.NewEggScraper;

public class App {
	//Instantiates the database connections
	Products db = new Products();
	CheckStock check = new CheckStock();
	InStockLogs logs = new InStockLogs();
	
	//Creates a window
	private Stage stage;
	private Scene scene;
	private BorderPane borderPane;
	
	private final int WINDOW_WIDTH = 1300;	//Default window width
	private final int WINDOW_HEIGHT = 850;	//Default window height
	
	public static boolean running = false;	//Static boolean to determine if the app is checking the stock
	
	//Initializes all global buttons used
	Button tableBtn;
	Button aboutBtn;
	Button settingsBtn;
	Button runBtn;
	Button addBtn;
	Button removeBtn;
	Button logsBtn;
	Button saveSettingsBtn;
	Button clearLogsBtn;
	
	public App() {
		stage = new Stage();	//Initializes the stage
		stage.getIcons().add(new Image("/imgs/CheckIcon.png"));	//Adds the icon to the top of the window screen
		buildApp();	//Builds the gui
	}
	
	private void buildApp() {
		borderPane = new BorderPane();	//Use border pane to structure window
		scene = new Scene(borderPane, WINDOW_WIDTH, WINDOW_HEIGHT);	//Initializes the scene
		
		//Sets the left, center and bottom panes of the window
		borderPane.setLeft(getLeftPane());
		borderPane.setCenter(getTablePane());
		borderPane.setBottom(getBottomPane());
		stage.setTitle("Restock - Wishlist");
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(e -> System.exit(0));	//Correctly exits the app if the user closes the window
	}
	
	private HBox getBottomPane() {
		HBox hbox = new HBox(5);	//Uses a horizontal box structure for the bottom pane
		hbox.setMinWidth(WINDOW_WIDTH);
		
		//Creates images for icons
		ImageView runImg = new ImageView(new Image("/imgs/RunIcon.png", 48, 48, false, true));
		ImageView pauseImg = new ImageView(new Image("/imgs/PauseIcon.png", 48, 48, false, true));
		ImageView addImg = new ImageView(new Image("/imgs/AddIcon.png", 32, 32, false, true));
		ImageView removeImg = new ImageView(new Image("/imgs/RemoveIcon.png", 32, 32, false, true));
		
		//Initialize buttons 
		runBtn = new Button();
		addBtn = new Button();
		removeBtn = new Button();
		
		runBtn.setStyle("-fx-background-color:#00ff00;");
		addBtn.setStyle("-fx-background-color:#ffffff;");
		removeBtn.setStyle("-fx-background-color:#ffffff;");
		
		runBtn.setGraphic(runImg);
		addBtn.setGraphic(addImg);
		removeBtn.setGraphic(removeImg);
		
		runBtn.setMinSize(120, 35);
		addBtn.setMinSize(100, 30);
		removeBtn.setMinSize(100, 30);
		
		hbox.setAlignment(Pos.CENTER);
		
		addBtn.setOnAction(e -> {	//If the add button is clicked then switch to the add item view 
			borderPane.setCenter(getNewItemPane());
			borderPane.setRight(getRightPane());
		});
		removeBtn.setOnAction(e -> {	//If the remove button is clicked then switch to the remove item view
			borderPane.setCenter(getRemovePane());
			borderPane.setRight(null);
		});
		
		//The run button is a toggle button so if clicked the state switches from run to stop and stop to run
		runBtn.setOnAction(e -> {
			if (!running) {	//If not running before button is clicked
				running = true;
				runBtn.setGraphic(pauseImg);	//Set the button image to pause 
				runBtn.setStyle("-fx-background-color:#aa1010;");
				buttonsEnabled(true);	//Does not allow the user to click on other buttons while the app is checking items in wish list 
				new Thread(check).start();	//Starts checking if items in wish list are in stock
				
			}
			else {
				running = false;
				runBtn.setGraphic(runImg);	//If was running while button is clicked stop running and display the run button for user
				runBtn.setStyle("-fx-background-color:#00ff00;");
				buttonsEnabled(false);	//Allows the user to click buttons again
			}
		});
		
		hbox.getChildren().addAll(addBtn, runBtn, removeBtn);
		hbox.setPadding(new Insets(8));
		hbox.setStyle("-fx-background-color:#222831;");
		return hbox;
	}
	
	private VBox getLeftPane() {
		VBox vbox = new VBox(12);
		
		//Images for icons 
		ImageView tableImg = new ImageView(new Image("imgs/ListIcon.png", 32, 32, false, true));
		ImageView aboutImg = new ImageView(new Image("imgs/AboutIcon.png", 32, 32, false, true));
		ImageView logsImg = new ImageView(new Image("imgs/LogsIcon.png", 32, 32, false, true));
		ImageView settingsImg = new ImageView(new Image("imgs/SettingsIcon.png", 32, 32, false, true));
		
		//Initialize buttons 
		tableBtn = new Button();
		aboutBtn = new Button();
		logsBtn = new Button();
		settingsBtn = new Button();
		
		//Style the buttons
		tableBtn.setStyle("-fx-background-color:#ffffff;");
		aboutBtn.setStyle("-fx-background-color:#ffffff;");
		logsBtn.setStyle("-fx-background-color:#ffffff;");
		settingsBtn.setStyle("-fx-background-color:#ffffff;");
		
		//Structure the size of the buttons
		tableBtn.setMinSize(70, 40);
		aboutBtn.setMinSize(70, 40);
		logsBtn.setMinSize(70, 40);
		settingsBtn.setMinSize(70, 40);
	
		//Set the icons to the images crated
		tableBtn.setGraphic(tableImg);
		aboutBtn.setGraphic(aboutImg);
		logsBtn.setGraphic(logsImg);
		settingsBtn.setGraphic(settingsImg);
		
		tableBtn.setOnAction(e -> {	//If table button is clicked display table that shows users wish list
			borderPane.setCenter(getTablePane());
			borderPane.setBottom(getBottomPane());
			borderPane.setRight(null);
		});
		aboutBtn.setOnAction(e -> {	//If about button is change to about view
			borderPane.setCenter(getAboutPane());
			borderPane.setBottom(null);
			borderPane.setRight(null);
		});
		settingsBtn.setOnAction(e -> {	//If settings button is clicked changed to settings view
			borderPane.setCenter(getSettingsPane());
			borderPane.setBottom(null);
			borderPane.setRight(null);
		});
		logsBtn.setOnAction(e -> {	//If the logs button is clicked switch to the logs table view
			borderPane.setCenter(getLogsPane());
			borderPane.setBottom(getLogsBottom());
			borderPane.setRight(null);
		});
		vbox.getChildren().addAll(tableBtn, aboutBtn, logsBtn, settingsBtn);		
		vbox.setPadding(new Insets(8));
		vbox.setStyle("-fx-background-color:#222831;");
		return vbox;
	}

	private VBox getRightPane() {	//Displays what web sites are supported with this app
		VBox vbox = new VBox(12);
		
		//Creates a title on the top left
		Label title = new Label("Supported Websites");
		title.setFont(new Font("Arial", 20));
		title.setTextFill(Color.WHITE);
		
		//Creates a list of web sites that are supported with this app
		Label list = new Label("- Amazon \n - Bestbuy \n - NewEgg");
		list.setFont(new Font("Arial", 18));
		list.setTextFill(Color.WHITE);
		
		vbox.getChildren().addAll(title, list);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.setPadding(new Insets(20));
		vbox.setStyle("-fx-background-color:#222831;");
		return vbox;
	}

	private void buttonsEnabled(boolean state) {	//Helper method that turns buttons on or off
		tableBtn.setDisable(state);
		aboutBtn.setDisable(state);
		settingsBtn.setDisable(state);
		removeBtn.setDisable(state);
		addBtn.setDisable(state);
		logsBtn.setDisable(state);
	}
	
	@SuppressWarnings("unchecked")
	private TableView<Product> getTablePane() {	
		stage.setTitle("Restock - Wishlist");
		ObservableList<Product> list = FXCollections.observableArrayList();	//Gets a list of all items in wishlist
		list.addAll(db.getProducts());
		TableView<Product> table;
		
		TableColumn<Product, String> itemColumn = new TableColumn<>("Item");	//First column is set to item and displays every item in the database
		itemColumn.setMinWidth(150);
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
		TableColumn<Product, String> priceColumn = new TableColumn<>("Price");	//Second column displays items price
		priceColumn.setMinWidth(150);
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		TableColumn<Product, Boolean> inStockColumn = new TableColumn<>("In Stock");	//Third column displays if item is in stock
		inStockColumn.setMinWidth(150);
		inStockColumn.setCellValueFactory(new PropertyValueFactory<>("inStock"));
		TableColumn<Product, String> websiteColumn = new TableColumn<>("Website");	//Fourth column displays the web site name 
		websiteColumn.setMinWidth(200);
		websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
		TableColumn<Product, String> ratingsColumn = new TableColumn<>("Ratings");	//Fifth column displays the ratings for every item
		ratingsColumn.setMinWidth(200);
		ratingsColumn.setCellValueFactory(new PropertyValueFactory<>("ratings"));
		TableColumn<Product, String> linkColumn = new TableColumn<>("Link");	//Sixth column displays links for every item 
		linkColumn.setCellValueFactory(new PropertyValueFactory<>("link"));
		linkColumn.setMinWidth(350);
		
		table = new TableView<>();
		table.getSelectionModel().cellSelectionEnabledProperty().set(true);
		
		table.setItems(list);
		table.getColumns().addAll(itemColumn, priceColumn, inStockColumn, websiteColumn, ratingsColumn, linkColumn);
		table.getSelectionModel().setCellSelectionEnabled(true);
		table.setOnMouseClicked(e -> {	//If the user clicks on the link cell it will open that link with their default browser
			try {	
			TablePosition<Product, String> pos = table.getSelectionModel().getSelectedCells().get(0);
				if (pos.getColumn() == 5 && Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
					Desktop.getDesktop().browse(new URI(table.getSelectionModel().getSelectedItem().getLink()));
				}
			} catch(Exception e1) {}
		});
		return table;
	}
	
	private VBox getNewItemPane() {
		VBox vbox = new VBox(12);
		
		stage.setTitle("Restock - Add Item");
		
		//Labels 
		Label title = new Label("Add New Item To Wishlist");
		title.setFont(new Font("Arial", 24));
		title.setTextFill(Color.BLACK);
		
		Label alert = new Label("Uploading item to wishlist ...");
		alert.setFont(new Font("Arial", 16));
		alert.setTextFill(Color.BLACK);
		alert.setVisible(false);
		
		Label status = new Label();
		status.setFont(new Font("Arial", 16));
		
		//TextFields
		TextField itemName = new TextField();
		itemName.setPromptText("Item Name:");
		itemName.setMaxWidth(500);
		itemName.setMinHeight(25);
		
		TextField siteLink = new TextField();
		siteLink.setPromptText("Website Link:");
		siteLink.setMaxWidth(500);
		
		Button saveBtn = new Button("Save");
		saveBtn.setOnAction(e -> {	//If button is clicked try to add new item to wish list
			if (itemName.getText().length() < 1) {	//Makes sure item name is longer than 1 character
				status.setText("Please enter a name for the item.");
				status.setTextFill(Color.RED);
				return;
			}
			if (siteLink.getText().length() < 1) {	//Makes sure the web site link is longer than one character
				status.setText("Please enter the website link of the item you would like to track.");
				status.setTextFill(Color.RED);
				return;
			}
			alert.setVisible(true);
			if (tryAmazon(itemName.getText(), siteLink.getText())) {	//Checks if link is a amazon link
				status.setText("New Item " + itemName.getText() + " is now added to your wishlist." );
				status.setTextFill(Color.GREEN);
			}
			else if (tryBestBuy(itemName.getText(), siteLink.getText())) {	//Checks if link is a best buy link
				status.setText("New Item " + itemName.getText() + " is now added to your wishlist." );	
				status.setTextFill(Color.GREEN);
			}
			else if (tryNewEgg(itemName.getText(), siteLink.getText())) {	//Checks if link is a new egg link
				status.setText("New Item " + itemName.getText() + " is now added to your wishlist." );
				status.setTextFill(Color.GREEN);
			}
			else {	//If link is not an amazon, best buy or new egg link, display an error message
				status.setText("Could not add item to wishlist. Please enter a link from a website that works with this app." );
				status.setTextFill(Color.RED);
			}
		});
		vbox.getChildren().addAll(title, itemName, siteLink, saveBtn, alert, status);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.setPadding(new Insets(10));
		return vbox;
	}
	
	private boolean tryAmazon(String itemName, String link) {	//Helper method that checks if link is an amazon link and upload new product
		AmazonScraper amz = new AmazonScraper(itemName, link);
		if (amz.isValidWebsite()) {//Calls a method from scraper class to see if is valid link
			amz.uploadProducts();//If it is a valid link upload that new product
			return true;
		}
		return false;
	}
	
	private boolean tryBestBuy(String itemName, String link) {	//Helper method that checks if link is a best buy link and upload new product
		BestBuyScraper bb = new BestBuyScraper(itemName, link);
		if (bb.isValidWebsite()) {	//Calls a method from scraper class to see if is valid link
			bb.uploadProducts();	//If it is a valid link upload that new product
			return true;
		}
		return false;
	}
	
	private boolean tryNewEgg(String itemName, String link) {	//Helper method that checks if link is a new egg link and upload new product
		NewEggScraper newEgg = new NewEggScraper(itemName, link);
		if (newEgg.isValidWebsite()) {//Calls a method from scraper class to see if is valid link
			newEgg.uploadProducts();//If it is a valid link upload that new product
			return true;
		}
		return false;
	}
	
	private VBox getRemovePane() {
		VBox vbox = new VBox(12);
		stage.setTitle("Restock - Remove");
		Label title = new Label("Remove Item From Wishlist");
		title.setFont(new Font("Arial", 24));
		title.setTextFill(Color.BLACK);
		
		//Labels
		Label alert = new Label("Removing item from wishlist ...");
		alert.setFont(new Font("Arial", 16));
		alert.setTextFill(Color.BLACK);
		alert.setVisible(false);
		Label status = new Label();
		status.setFont(new Font("Arial", 16));
		
		//TextField
		TextField itemName = new TextField();
		itemName.setPromptText("Item Name:");
		itemName.setMaxWidth(500);
		itemName.setMinHeight(25);
		
		Button deleteBtn = new Button("Delete");
		deleteBtn.setOnAction(e -> {	//If button is clicked try to delete every item with the same name from the wish list
			alert.setVisible(true);
			if (db.deleteOneProduct(itemName.getText())) {	//Deletes the document from the data base
				status.setTextFill(Color.GREEN);
				status.setText("Successfully removed all instances of " + itemName.getText() + " from your wishlist");
			}
			else {	//If item is not found display an error message
				status.setTextFill(Color.RED);
				status.setText("Could not remove " + itemName.getText() + " from your wishlist. Does it exist?");
			}
		});
		
		//Labels
		Label clearTitle = new Label("Clear Wishlist");
		clearTitle.setFont(new Font("Arial", 24));
		clearTitle.setTextFill(Color.BLACK);
		Label clearAlert = new Label("Removing all item from wishlist ...");
		clearAlert.setFont(new Font("Arial", 16));
		clearAlert.setTextFill(Color.BLACK);
		clearAlert.setVisible(false);
		Label clearStatus = new Label();
		clearStatus.setFont(new Font("Arial", 16));
		
		Button clearBtn = new Button("Clear Wishlist");
		clearBtn.setOnAction(e -> {	//If this button is clicked delete everything from the wish list
			clearAlert.setVisible(true);
			if (db.deleteAllProducts()) {
				clearStatus.setText("Successfully cleared wishlist");
				clearStatus.setTextFill(Color.GREEN);
			}
			else {
				clearStatus.setText("Could not clear wishlist");
				clearStatus.setTextFill(Color.RED);
			}
		});
		
		vbox.getChildren().addAll(title, itemName, deleteBtn, alert, status, clearTitle, clearBtn, clearAlert, clearStatus);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.setPadding(new Insets(10));
		return vbox;
	}
	
	private VBox getAboutPane() {	//A view that just displays text explaining how to use the app
		VBox vbox = new VBox(12);
		stage.setTitle("Restock - About");;
		
		//Label
		Label title = new Label("About");
		title.setFont(new Font("Arial", 30));
		title.setTextFill(Color.BLACK);
		title.setPadding(new Insets(0, 0, 20, 0));
		
		//Texts
		Text addTitle = new Text("Adding New Item To Wishlist");
		addTitle.setFont(new Font("Arial", 22));
		addTitle.setFill(Color.BLACK);
		Text addMessage = new Text("To add a new item to your wishlist click on the table button on the left navigation bar, "
				+ "and select the add button on the bottom. Once you are in the add menu type a name for the item and select what website it is from. "
				+ "Enter the link for the item and press save. If everything was entered correctly you will see a success message and will see a new item"
				+ " added to your table.");
		addMessage.setFont(new Font("Arial", 14));
		addMessage.setFill(Color.BLACK);
		addMessage.setWrappingWidth(750);
		
		Text removeTitle = new Text("Removing An Item From Your Wishlist");
		removeTitle.setFont(new Font("Arial", 22));
		removeTitle.setFill(Color.BLACK);
		Text removeMessage = new Text("To remove an item from your wishlist click on the wishlist button on the left, then click on the remove button on"
				+ " the bottom. After that you can neither type the name of the item you want to delete or select clear wishlist. If you type the name "
				+ "of the item you want to remove from your wishlist, all items under that name will be deleted. Clearing the wishlist will remove everything.");
		removeMessage.setFont(new Font("Arial", 14));
		removeMessage.setFill(Color.BLACK);
		removeMessage.setWrappingWidth(750);
		
		Text checkingWishListTitle = new Text("Checking Wishlist");
		checkingWishListTitle.setFont(new Font("Arial", 22));
		checkingWishListTitle.setFill(Color.BLACK);
		Text wishlistMessage = new Text("Your wishlist is checked every 30 seconds. If an item is found the app will notify you via a windows notification, and add a new entry to your logs."
				+ "Please keep in mind that your wishlist is checked every 30 seconds, so if you have a slower pc make sure your wishlist is not too large.");
		wishlistMessage.setFont(new Font("Arial", 14));
		wishlistMessage.setFill(Color.BLACK);
		wishlistMessage.setWrappingWidth(750);
		
		Text settingsTitle = new Text("Settings");
		settingsTitle.setFont(new Font("Arial", 22));
		settingsTitle.setFill(Color.BLACK);
		Text settingsMessage = new Text("You can manage your settins by clicking the icon on the left. Two settings can be changed, receive windows notifications and keep logs of items in stock. "
				+ "Receiveing windows notifications will display a windows notification when an item in your wishlist is in stock. Keep in mind that windows notifications do not display when another application is running in fuillscreen mode. "
				+ "Keep logs will add a new entry of the time and price everyime an item that is in your wishlist comes in stock.");
		settingsMessage.setFont(new Font("Arial", 14));
		settingsMessage.setFill(Color.BLACK);
		settingsMessage.setWrappingWidth(750);
	
		
		vbox.getChildren().addAll(title, addTitle, addMessage, removeTitle, removeMessage, checkingWishListTitle, wishlistMessage, settingsTitle, settingsMessage);
		vbox.setPadding(new Insets(10, 0, 0, 20));
		return vbox;
	}
	
	private VBox getSettingsPane() {	//Display two settings options to the user: Keep logs and receive windows notifications
		VBox vbox = new VBox(12);
		stage.setTitle("Restock - Settings");;
		Settings settings = new Settings();
		
		Label title = new Label("Settings");
		title.setFont(new Font("Arial", 30));
		title.setTextFill(Color.BLACK);
		title.setPadding(new Insets(0, 0, 20, 0));
		
		Label alerts = new Label("Alerts");
		alerts.setFont(new Font("Arial", 26));
		alerts.setTextFill(Color.BLACK);

		CheckBox windowsAlerts = new CheckBox("Get Windows Notifications");
		
		Label logs = new Label("Logs");
		logs.setFont(new Font("Arial", 26));
		logs.setTextFill(Color.BLACK);
		CheckBox logsCheckBox = new CheckBox("Keep Logs Of Items In Stock");
		logsCheckBox.setPadding(new Insets(0, 0, 20, 0));
		
		//Check settings collection to see if user has these options checked on
		windowsAlerts.setSelected(settings.getWindowsAlertOption());	
		logsCheckBox.setSelected(settings.getKeepLogsOption());
		
		Label checkInterval = new Label("Update Wishlist Interval (Seconds)");
		checkInterval.setFont(new Font("Arial", 26));
		checkInterval.setTextFill(Color.BLACK);
		
		Label status = new Label();
		status.setFont(new Font("Arial", 18));
		
		Label newSettings = new Label();
		newSettings.setFont(new Font("Arial", 18));
		newSettings.setTextFill(Color.BLACK);
		
		saveSettingsBtn = new Button("Save");
		saveSettingsBtn.setOnAction(e -> {	//Once user clicks the save button the check box values are saved in the database
			settings.setWindowsAlert(windowsAlerts.isSelected());
			settings.setKeepLogs(logsCheckBox.isSelected());
			status.setText("New Settings Saved");
			status.setTextFill(Color.GREEN);
			newSettings.setText("Windows Alert Set To: " + windowsAlerts.isSelected() + "\nKeep Logs Set To: " + logsCheckBox.isSelected());
		});
		
		vbox.getChildren().setAll(title, alerts, windowsAlerts, logs, logsCheckBox, checkInterval, saveSettingsBtn, status, newSettings);
		vbox.setPadding(new Insets(10, 0, 0, 20));
		return vbox;
	}
	
	@SuppressWarnings("unchecked")
	private TableView<Product> getLogsPane() {	//Displays a table showing the logs of items that were in stock
		stage.setTitle("Restock - Logs");
		ObservableList<Product> list = FXCollections.observableArrayList();
		list.addAll(logs.getLogs());
		TableView<Product> table;
		
		TableColumn<Product, String> itemColumn = new TableColumn<>("Item");	//First column displays item name
		itemColumn.setMinWidth(150);
		itemColumn.setCellValueFactory(new PropertyValueFactory<>("item"));
		TableColumn<Product, String> priceColumn = new TableColumn<>("Price");	//Second column displays items price
		priceColumn.setMinWidth(150);
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		TableColumn<Product, String> websiteColumn = new TableColumn<>("Website");	//Third column displays items web site
		websiteColumn.setMinWidth(200);
		websiteColumn.setCellValueFactory(new PropertyValueFactory<>("website"));
		TableColumn<Product, String> timeColumn = new TableColumn<>("Time");	//Fourth item displays when the item was in stock
		timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
		timeColumn.setMinWidth(200);
		
		table = new TableView<>();
		table.getSelectionModel().cellSelectionEnabledProperty().set(true);
		
		table.setItems(list);
		table.getColumns().addAll(itemColumn, priceColumn, websiteColumn, timeColumn);
		
		return table;
	}
	
	private HBox getLogsBottom() {	//Button to clear the logs
		HBox hbox = new HBox(5);
		hbox.setMinWidth(WINDOW_WIDTH);
		
		clearLogsBtn = new Button("Clear Logs");
		clearLogsBtn.setOnAction(e -> {	//Once this button is clicked every item in the logs collection in the data base will be deleted 
			logs.clearLogs();
			borderPane.setCenter(getLogsPane());	//Refresh the logs screen so the user can see that every log has been deleted
		});
		
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(clearLogsBtn);
		hbox.setPadding(new Insets(8));
		hbox.setStyle("-fx-background-color:#222831;");
		return hbox;
	}
}














