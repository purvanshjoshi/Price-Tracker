package com.pricetracker.engine;

import com.pricetracker.database.DatabaseManager;
import com.pricetracker.model.Product;
import com.pricetracker.scrapers.AmazonScraper;
import com.pricetracker.scrapers.FlipkartScraper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.*;

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
            // Example for future extension:
            // Callable<Product> chromaTask = new ChromaScraper(searchKeyword);

            // 2. Submit tasks to the Executor (they start running simultaneously right now!)
            List<Future<Product>> futures = new ArrayList<>();
            futures.add(executorService.submit(amazonTask));
            futures.add(executorService.submit(flipkartTask));
            
            // 3. Wait for the threads to finish and collect results
            for (Future<Product> future : futures) {
                try {
                    // .get() will block until this specific thread is finished finding the price
                    Product result = future.get();
                    
                    // We only want valid products (ignore errors marked as Double.MAX_VALUE)
                    if (result != null && result.getPrice() < Double.MAX_VALUE) {
                        
                        // Insert the product into the TreeMap to automatically sort it
                        sortedResults.put(result.getPrice(), result);
                        
                        // Save the permanent history directly to the MySQL database!
                        databaseManager.savePriceHistory(result); 
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
