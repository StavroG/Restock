# Restock
**Description:** Restock is an app that allows the user to enter a link of a product from either; Amazon, NewEgg or BestBuy. Once the user adds the link that item is added to the users wish list. The wish list keeps track of a few fields including; is the item in stock and the price of that item. The data for every item is updated every 30 seconds and if the user wishes so the app will notify the user if an item from their wishlist is in stock and from what website. The app can also do some other things like keep a log of every time an item comes in stock, which could be useful for collecting data. 

### What I Learned
- I learned how to scrape information from websites using JSoup.
- I learned how to create a better GUI using JavaFX and used it to make a tables to display data.
- I learned how to use multi-threading in Java to update the GUI while scraping the web at the same time.

### Requirements
- Java - Most computers and other applications already use Java, but if you do not have Java you can download it from (https://java.com/en/).
- MongoDB - The app uses MongoDB to keep track of every item and the users specific request. If you do not have MongoDB installed you can download the community version from (https://www.mongodb.com/try/download/community).
- Windows - Windows 10 is recommended, but other versions of Windows will work.

### Add Item
- Copy a link from any item on either; Amazon, NewEgg or BestBuy.
- Go to your wish list and press the add button on the bottom. 
- Give your item a name, paste the link and press save.
- Press the run button to start the wish list check. The wish list will be checked every 30 seconds.

### Delete Item
- Go to your wish list and press the delete button on the bottom.
- Type the name you gave the item in your wish list and press remove. Every item with that same name will be deleted from your wish list.

### Notifications and Logs
- Go to settings by clicking on the settings button on the left.
- If you want to receive a Windows notification every time an item from your wish list is in stock, check the notification box.
- If you want to add a new item to your logs, check the logs box.


### Note: This app is not very demanding for most Windows computers, but wish lists over the size of 100, while they are possible, are not recommended. Also if you decide to receive Windows notifications when an item from your wish list is in stock remember that Windows, by default, will not send the notification if an application is running in full screen mode.
