package alerts;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;

import org.bson.Document;

public class WindowsAlert {
	public void alert(Document doc) throws AWTException {
		if (SystemTray.isSupported()) {	//If is can send a windows alert
			SystemTray tray = SystemTray.getSystemTray();	//Creates a new tray so we can add the notification
			
			Image image = Toolkit.getDefaultToolkit().createImage("");	//Creates an empty image
			TrayIcon icon = new TrayIcon(image);	//Creates an icon with the empty image, so the icon is just blank
			icon.setImageAutoSize(true);
			tray.add(icon);	//Adds the blank icon to the tray
			
			icon.displayMessage("Restock!", getMessage(doc), MessageType.NONE);	//Displays the message as a windows notification
		}
	}
	
	private String getMessage(Document doc) {
		//Gets the info of a item from the database to be displayed as a windows notification
		return doc.getString("item") + " is now in stock at " + doc.getString("website") + " for " + doc.getString("price");
	}
}
