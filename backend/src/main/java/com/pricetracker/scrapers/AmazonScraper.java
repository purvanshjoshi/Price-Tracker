package com.pricetracker.scrapers;

import com.pricetracker.model.Product;
import com.pricetracker.engine.SelectorConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class AmazonScraper implements ScraperCallable {

    private final String searchKeyword;

    public AmazonScraper(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    @Override
    public Product scrape(String keyword) {
        WebDriver driver = null;
        try {
            // 1. Setup Stealth Driver
            driver = com.pricetracker.engine.WebDriverFactory.createStealthDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

            // 2. Visit homepage first to establish cookies and avoid 503 bot detection
            driver.get("https://www.amazon.in/");
            Thread.sleep(1500 + new java.util.Random().nextInt(1500));

            // 3. Navigate to search URL
            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.amazon.in/s?k=" + encodedQuery;
            driver.get(url);

            // 4. Wait for products to load with fallback selectors
            String wrapperSelector = SelectorConfig.get("amazon", "product_wrapper");
            // Add fallback selector in case the primary one doesn't work
            String combinedWrapper = wrapperSelector + ", .s-result-item[data-asin]:not([data-asin=''])";
            
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(combinedWrapper)));
            } catch (Exception e) {
                System.err.println("[Amazon] Failed to find results. Page Title: " + driver.getTitle());
                System.err.println("[Amazon] URL: " + driver.getCurrentUrl());
                String source = driver.getPageSource();
                if (source.contains("captcha") || source.contains("Robot Check")) {
                    System.err.println("[Amazon] Bot detection triggered!");
                } else if (source.contains("503") || source.contains("Service Unavailable")) {
                    System.err.println("[Amazon] 503 Service Unavailable - blocked by Amazon!");
                }
                throw e;
            }

            // 5. Get all matching result elements and iterate to find a valid one
            java.util.List<WebElement> results = driver.findElements(By.cssSelector(combinedWrapper));
            
            String titleSelector = SelectorConfig.get("amazon", "title");
            String priceSelector = SelectorConfig.get("amazon", "price");
            String linkSelector = SelectorConfig.get("amazon", "link");

            for (WebElement result : results) {
                try {
                    // Title
                    String title;
                    try {
                        title = result.findElement(By.cssSelector(titleSelector)).getText().trim();
                    } catch (Exception e) {
                        // Fallback: try h2 span
                        try {
                            title = result.findElement(By.cssSelector("h2 span")).getText().trim();
                        } catch (Exception e2) {
                            continue; // Skip this result, no title found
                        }
                    }
                    if (title.isEmpty()) continue;

                    // Price
                    double price = Double.MAX_VALUE;
                    try {
                        String priceText = result.findElement(By.cssSelector(priceSelector))
                                .getText().replaceAll("[^0-9.]", "");
                        if (!priceText.isEmpty()) {
                            price = Double.parseDouble(priceText);
                        }
                    } catch (Exception e) {
                        // Try fallback price selectors
                        try {
                            String priceText = result.findElement(By.cssSelector(".a-price .a-offscreen"))
                                    .getAttribute("textContent").replaceAll("[^0-9.]", "");
                            if (!priceText.isEmpty()) {
                                price = Double.parseDouble(priceText);
                            }
                        } catch (Exception e2) {
                            continue; // Skip result without price
                        }
                    }
                    if (price == Double.MAX_VALUE) continue;

                    // Link
                    String productLink = url;
                    try {
                        WebElement linkEl = result.findElement(By.cssSelector(linkSelector));
                        String href = linkEl.getAttribute("href");
                        if (href != null && !href.isEmpty()) {
                            productLink = href;
                        }
                    } catch (Exception e) {
                        // Keep the search URL as fallback
                    }

                    return new Product(title, price, "Amazon", productLink);
                } catch (Exception e) {
                    // This result failed, try the next one
                    continue;
                }
            }

        } catch (Exception e) {
            System.err.println("Amazon Selenium Scraper Failed: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return new Product(keyword + " (Not Found)", Double.MAX_VALUE, "Amazon", "");
    }

    @Override
    public Product call() throws Exception {
        return scrape(searchKeyword);
    }
}
