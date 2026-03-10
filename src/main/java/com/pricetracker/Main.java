package com.pricetracker;

import com.pricetracker.engine.EngineManager;
import com.pricetracker.engine.MessageParser;
import com.pricetracker.model.Product;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.TreeMap;

public class Main {
    public static void main(String[] args) {
        
        // 1. Initialize our Multithreading web scraper engine
        EngineManager engine = new EngineManager();

        // 2. Start the infinite loop to listen to Google Chrome
        while (true) {
            try {
                // Read the incoming byte-prefixed JSON from Standard Input
                String incomingMessage = MessageParser.readMessage(System.in);
                
                // Chrome closed the connection, so we shut down the Java app safely
                if (incomingMessage == null) {
                    break;
                }

                // incomingMessage looks like: {"query": "iPhone 15"}
                JSONObject jsonInput = new JSONObject(incomingMessage);
                
                if (jsonInput.has("query")) {
                    String query = jsonInput.getString("query");
                    
                    // 3. Command the engine to scrape Amazon & Flipkart concurrently
                    TreeMap<Double, Product> results = engine.executeSearch(query);

                    // 4. Format the result back into JSON for Chrome to read
                    JSONArray outputArray = new JSONArray();
                    for (Product p : results.values()) {
                        outputArray.put(p.toJSON());
                    }
                    
                    JSONObject finalOutput = new JSONObject();
                    finalOutput.put("results", outputArray);

                    // 5. Send the exact byte-formatted JSON back to Chrome via Standard Output
                    MessageParser.sendMessage(System.out, finalOutput);
                }

            } catch (Exception e) {
                // If anything crashes, we MUST NOT print to System.out, 
                // otherwise Chrome Native Messaging will completely freeze since it 
                // expects a 4-byte prefixed integer, not a Java StackTrace.
                System.err.println("Fatal Error in Main Loop: " + e.getMessage());
            }
        }
    }
}
