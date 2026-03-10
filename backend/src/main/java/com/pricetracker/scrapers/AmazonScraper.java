package com.pricetracker.scrapers;

import com.pricetracker.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
            // We spoof the User-Agent so Amazon doesn't block us as a bot
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .timeout(5000)
                    .get();

            // 3. Extract the first product result from the page
            // Amazon specific CSS wrapper for search results
            Element firstResult = doc.selectFirst("div[data-component-type='s-search-result']");
            
            if (firstResult != null) {
                // Extract Name
                Element titleElement = firstResult.selectFirst("h2 a span");
                String title = (titleElement != null) ? titleElement.text() : "Unknown Product";

                // Extract Price
                Element priceElement = firstResult.selectFirst("span.a-price-whole");
                double price = 0.0;
                if (priceElement != null) {
                    // Amazon prices have commas (e.g. 75,000) that need to be removed to parse to Double
                    String cleanPrice = priceElement.text().replace(",", "").replace("₹", "").trim();
                    try {
                        price = Double.parseDouble(cleanPrice);
                    } catch (NumberFormatException e) {
                        System.err.println("Amazon Price Parsing Error: " + cleanPrice);
                    }
                }

                // Extract specific product Link
                Element linkElement = firstResult.selectFirst("h2 a");
                String productLink = url; 
                if (linkElement != null) {
                    productLink = "https://www.amazon.in" + linkElement.attr("href");
                }

                // Return the neat Product object back to our Engine
                return new Product(title, price, "Amazon", productLink);
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
