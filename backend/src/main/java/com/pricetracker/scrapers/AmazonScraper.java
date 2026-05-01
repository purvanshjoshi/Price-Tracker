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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // 2. Navigate to Amazon
            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.amazon.in/s?k=" + encodedQuery;
            
            // Random delay to mimic human behavior
            Thread.sleep(1000 + new java.util.Random().nextInt(2000));
            
            driver.get(url);

            // 3. Wait for products to load with diagnostics
            String wrapperSelector = SelectorConfig.get("amazon", "product_wrapper");
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(wrapperSelector)));
            } catch (Exception e) {
                System.err.println("[Amazon] Failed to find results. Page Title: " + driver.getTitle());
                System.err.println("[Amazon] URL: " + driver.getCurrentUrl());
                // Optional: Print a bit of page source for debugging
                String source = driver.getPageSource();
                if (source.contains("captcha") || source.contains("Robot Check")) {
                    System.err.println("[Amazon] Bot detection triggered!");
                }
                throw e;
            }

            WebElement firstResult = driver.findElement(By.cssSelector(wrapperSelector));
            
            if (firstResult != null) {
                // Title
                String title = "Unknown Amazon Product";
                try {
                    WebElement titleEl = firstResult.findElement(By.cssSelector("h2"));
                    title = titleEl.getText();
                } catch (Exception e) {
                    // Ignore missing title
                }

                // Price
                String priceSelector = SelectorConfig.get("amazon", "price");
                double price = Double.MAX_VALUE;
                try {
                    String priceText = firstResult.findElement(By.cssSelector(priceSelector)).getText().replaceAll("[^0-9.]", "");
                    if (!priceText.isEmpty()) {
                        price = Double.parseDouble(priceText);
                    }
                } catch (Exception e) {
                    // Ignore price parsing errors
                }

                // Link
                String productLink = url;
                try {
                    WebElement linkEl = firstResult.findElement(By.cssSelector("h2 a"));
                    productLink = linkEl.getAttribute("href");
                } catch (Exception e) {
                    // Ignore link retrieval errors
                }

                return new Product(title, price, "Amazon", productLink);
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
