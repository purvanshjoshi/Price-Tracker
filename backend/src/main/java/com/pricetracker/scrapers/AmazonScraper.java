package com.pricetracker.scrapers;

import com.pricetracker.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.pricetracker.engine.SelectorConfig;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class AmazonScraper implements ScraperCallable {
    
    private final String searchKeyword;

    public AmazonScraper(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    @Override
    public Product scrape(String keyword) {
        try {
            // 1. Format the URL with the search term
            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.amazon.in/s?k=" + encodedQuery;

            // 2. Connect via Jsoup and download HTML
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(5000)
                    .get();

            // 3. Extract the first product result from the page using dynamic selectors
            String wrapperSelector = SelectorConfig.get("amazon", "product_wrapper");
            Element firstResult = doc.selectFirst(wrapperSelector);
            
            if (firstResult != null) {
                // Extract Name
                String titleSelector = SelectorConfig.get("amazon", "title");
                Element titleElement = firstResult.selectFirst(titleSelector);
                String title = (titleElement != null) ? titleElement.text() : "Unknown Product";

                // Extract Price
                String priceSelector = SelectorConfig.get("amazon", "price");
                Element priceElement = firstResult.selectFirst(priceSelector);
                double price = 0.0;
                if (priceElement != null) {
                    String cleanPrice = priceElement.text().replace(",", "").replace("₹", "").trim();
                    try {
                        price = System.getProperty("os.name").contains("Windows") ? Double.parseDouble(cleanPrice) : Double.parseDouble(cleanPrice);
                        // Standardize the parsing
                        price = Double.parseDouble(cleanPrice);
                    } catch (NumberFormatException e) {
                        System.err.println("[Amazon] Price Parsing Error: " + cleanPrice);
                    }
                }

                // Extract Specific Link
                String linkSelector = SelectorConfig.get("amazon", "link");
                Element linkElement = firstResult.selectFirst(linkSelector);
                String productLink = url; 
                if (linkElement != null) {
                    productLink = "https://www.amazon.in" + linkElement.attr("href");
                }

                return new Product(title, price, "Amazon", productLink);
            } else {
                System.err.println("[Amazon] No product card found using selector: " + wrapperSelector);
            }

        } catch (Exception e) {
             System.err.println("Amazon Scraper Failed: " + e.getMessage());
        }
        
        // Return a broken product if scraping failed
        return new Product(keyword + " (Not Found)", Double.MAX_VALUE, "Amazon", "");
    }

    // This is the method required by the Callable interface
    @Override
    public Product call() throws Exception {
        return scrape(this.searchKeyword);
    }
}
