package com.pricetracker.engine;

import com.pricetracker.database.DatabaseManager;
import com.pricetracker.model.Product;
import com.pricetracker.scrapers.AmazonScraper;
import com.pricetracker.scrapers.FlipkartScraper;
import com.pricetracker.scrapers.RelianceScraper;
import com.pricetracker.scrapers.CromaScraper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class EngineManager {

    private final ExecutorService executorService;
    private final DatabaseManager databaseManager;

    public EngineManager() {
        // Create a fixed thread pool to run scrapers concurrently
        this.executorService = Executors.newFixedThreadPool(3);
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Executes the scrapers concurrently, sorts the results, saves them, and returns them.
     */
    public TreeMap<Double, Product> executeSearch(String searchKeyword) {
        
        // A TreeMap automatically sorts its keys natively in Java. 
        // We use Price as the Key so the cheapest product is always first!
        TreeMap<Double, Product> sortedResults = new TreeMap<>();

        try {
            // 1. Prepare Scraper tasks
            Callable<Product> amazonTask = new AmazonScraper(searchKeyword);
            Callable<Product> flipkartTask = new FlipkartScraper(searchKeyword);
            Callable<Product> relianceTask = new RelianceScraper(searchKeyword);
            Callable<Product> cromaTask = new CromaScraper(searchKeyword);

            // 2. Submit tasks to the Executor (they start running simultaneously right now!)
            List<Future<Product>> futures = new ArrayList<>();
            futures.add(executorService.submit(amazonTask));
            futures.add(executorService.submit(flipkartTask));
            futures.add(executorService.submit(relianceTask));
            futures.add(executorService.submit(cromaTask));
            
            // 3. Wait for the threads to finish and collect results
            for (Future<Product> future : futures) {
                try {
                    // .get() will block until this specific thread is finished finding the price
                    Product result = future.get();
                    
                    if (result != null) {
                        if (result.getPrice() < Double.MAX_VALUE) {
                            // Insert the product into the TreeMap to automatically sort it
                            sortedResults.put(result.getPrice(), result);
                            
                            // Save the permanent history directly to the MySQL database!
                            databaseManager.savePriceHistory(result); 
                        } else {
                            // Soft block or Parse Error - Trigger Python Intelligence Service Challenge Layer
                            System.out.println("Fallback triggered for URL: " + result.getUrl());
                            try {
                                java.net.URL url = new java.net.URL("http://localhost:8000/v1/extract");
                                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json");
                                conn.setDoOutput(true);
                                
                                String jsonInputString = "{\"url\": \"" + result.getUrl() + "\"}";
                                try(java.io.OutputStream os = conn.getOutputStream()) {
                                    byte[] input = jsonInputString.getBytes("utf-8");
                                    os.write(input, 0, input.length);
                                }
                                
                                int code = conn.getResponseCode();
                                if (code == 200) {
                                    java.io.BufferedReader br = new java.io.BufferedReader(
                                        new java.io.InputStreamReader(conn.getInputStream(), "utf-8"));
                                    StringBuilder response = new StringBuilder();
                                    String responseLine = null;
                                    while ((responseLine = br.readLine()) != null) {
                                        response.append(responseLine.trim());
                                    }
                                    System.out.println("Intelligence Service Response: " + response.toString());
                                    // Normally we would deserialize to ExtractionResponse.java and retry
                                }
                            } catch (Exception apiEx) {
                                System.err.println("Intelligence Service failed: " + apiEx.getMessage());
                            }
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Error retrieving scraper result: " + e.getMessage());
                }
            }
        } finally {
             // In a normal application we would shutdown the executor, 
             // but since this engine is meant to stay alive for Chrome Native Messaging, 
             // we keep it open for future requests.
        }

        return sortedResults;
    }
}
