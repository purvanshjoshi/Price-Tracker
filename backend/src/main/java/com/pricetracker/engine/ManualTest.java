package com.pricetracker.engine;

import com.pricetracker.model.Product;
import com.pricetracker.scrapers.AmazonScraper;
import com.pricetracker.scrapers.FlipkartScraper;
import com.pricetracker.scrapers.RelianceScraper;
import com.pricetracker.scrapers.CromaScraper;

public class ManualTest {
    public static void main(String[] args) throws Exception {
        String keyword = "iPhone 15";
        System.out.println("--- Starting Manual Scraper Test for: " + keyword + " ---");
        
        // Config loads automatically via static block when first accessed.
        // No need for getInstance().

        System.out.println("\n[Testing Amazon]");
        AmazonScraper amazon = new AmazonScraper(keyword);
        Product amzResult = amazon.call();
        printProduct(amzResult);

        System.out.println("\n[Testing Flipkart]");
        FlipkartScraper flipkart = new FlipkartScraper(keyword);
        printProduct(flipkart.call());

        System.out.println("\n[Testing Reliance Digital]");
        RelianceScraper reliance = new RelianceScraper(keyword);
        printProduct(reliance.call());

        System.out.println("\n[Testing Croma]");
        CromaScraper croma = new CromaScraper(keyword);
        printProduct(croma.call());
    }

    private static void printProduct(Product p) {
        if (p == null) {
            System.out.println("Result is NULL");
            return;
        }
        if (p.getPrice() >= Double.MAX_VALUE) {
            System.out.println("Scraper Failed (Price is MAX_VALUE)");
        } else {
            System.out.println("Success!");
            System.out.println("Title: " + p.getName());
            System.out.println("Price: " + p.getPrice());
            System.out.println("URL: " + p.getUrl());
        }
    }
}
