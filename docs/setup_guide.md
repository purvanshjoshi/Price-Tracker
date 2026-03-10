# Price Tracker - Local Setup Guide

Follow these exact steps to compile the Java Engine, set up your Database, and install the Chrome Extension on your local machine.

---

## 1. Set Up the Database
**Note: You don't need to install anything!** 
The project has been converted to use **SQLite**. The database file (`price_history.db`) will be created automatically in the `backend/` folder the first time you run a search.

No server, no username, and no password required. It just works!

---

## 2. Compile the Java Engine
We need to package your Java code and all the dependencies (Jsoup, MySQL driver, JSON) into a single executable `PriceTrackerEngine.jar`.

1. Open a terminal or Command Prompt in the backend folder (`d:\Price Tracker\backend`).
2. Run the Maven packaging command:
   ```bash
   mvn clean package
   ```
3. Wait for it to say `BUILD SUCCESS`.
4. Verify that the file `target/PriceTrackerEngine-jar-with-dependencies.jar` exists.
   *Note: In our config, it will be named `target/PriceTrackerEngine.jar`.*

---

## 3. Register the Native Messaging Host (Windows)
Google Chrome needs permission to run your Java file. We do this by creating a quick Registry key.

1. Open **Chrome** and go to `chrome://extensions/`.
2. Turn on **Developer mode** (top right toggle).
3. Click **Load unpacked** and select the `d:\Price Tracker\extension` folder.
4. **Important:** Note the 32-character Extension ID that Chrome gives it (e.g., `abcdefghijklmnopqrstuvwxyz123456`).
5. Open `d:\Price Tracker\host-config\com.pricetracker.engine.json` and replace `<YOUR_EXTENSION_ID_HERE>` with your actual Extension ID. Save the file.
6. Open an **Administrator Command Prompt**.
7. Run this command to add the Registry Key (replace the path if you moved the project):
   ```cmd
   REG ADD "HKCU\Software\Google\Chrome\NativeMessagingHosts\com.pricetracker.engine" /ve /t REG_SZ /d "d:\Price Tracker\host-config\com.pricetracker.engine.json" /f
   ```

---

## 4. Test the Extension!
1. Click the **Price Scout (Avengers)** icon in your Chrome extensions menu.
2. Type a product name like `iPhone 15`.
3. Click **Track Prices**!
4. Check your MySQL `price_history` table—you should see the newly scraped prices magically appear!
