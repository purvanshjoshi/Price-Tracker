package com.pricetracker.scrapers;

import com.pricetracker.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class FlipkartScraper implements ScraperCallable {

    private final String searchKeyword;

    public FlipkartScraper(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    @Override
    public Product scrape(String keyword) {
        try {
            // 1. Format URL
            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.flipkart.com/search?q=" + encodedQuery;

            // 2. Fetch HTML
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(5000)
                    .get();

            // 3. Extract the first product result
            // Flipkart uses obfuscated CSS classes, but there are standard wrappers we can rely on.
            // A common wrapper for product cards in list view is usually an 'a' tag acting as a block, or a specific div class
            Element firstResult = doc.selectFirst("div[data-id]"); 
            
            if (firstResult != null) {
                // Determine Title
                // Flipkart commonly uses multiple different structures depending on the category.
                // We will try the most common class combinations for titles.
                Element titleElement = firstResult.selectFirst("div.KzDlHZ"); // Example updated class, these change frequently
                if (titleElement == null) { titleElement = firstResult.selectFirst("a[title]"); }
                
                String title = "Unknown Product";
                if (titleElement != null) {
                    title = titleElement.hasAttr("title") ? titleElement.attr("title") : titleElement.text();
                }

                // Determine Price
                Element priceElement = firstResult.selectFirst("div.Nx9bqj"); // Another frequently changing class for the main price
                double price = 0.0;
                if (priceElement != null) {
                    String cleanPrice = priceElement.text().replace("₹", "").replace(",", "").trim();
                     try {
                        price = Double.parseDouble(cleanPrice);
                    } catch (NumberFormatException e) {
                        System.err.println("Flipkart Price Parsing Error: " + cleanPrice);
                    }
                }

                // Determine URL
                Element linkElement = firstResult.selectFirst("a");
                String productLink = url;
                if (linkElement != null && linkElement.hasAttr("href")) {
                     productLink = "https://www.flipkart.com" + linkElement.attr("href");
                }

                return new Product(title, price, "Flipkart", productLink);
            }

        } catch (Exception e) {
            System.err.println("Flipkart Scraper Failed: " + e.getMessage());
        }
        
        return new Product(keyword + " (Not Found)", Double.MAX_VALUE, "Flipkart", "");
    }

    @Override
    public Product call() throws Exception {
        return scrape(this.searchKeyword);
    }
}
