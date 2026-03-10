package com.pricetracker.scrapers;

import com.pricetracker.model.Product;
import java.util.concurrent.Callable;

/**
 * All web scrapers (Amazon, Flipkart, etc.) must implement this interface.
 * We use Callable instead of Runnable because we need the thread 
 * to RETURN the Product result back to the main EngineManager.
 */
public interface ScraperCallable extends Callable<Product> {
    
    // The specific logic for scraping the website will be written here
    Product scrape(String searchKeyword);
}
