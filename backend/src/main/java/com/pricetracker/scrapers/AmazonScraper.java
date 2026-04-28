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
            // 1. Setup Headless Chrome
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new"); // Use new headless mode
            options.addArguments("--disable-gpu");
            options.addArguments("--no-sandbox");
            options.addArguments("--disable-dev-shm-usage");
            options.addArguments("--window-size=1920,1080");
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");

            // Stealth settings
            options.addArguments("--disable-blink-features=AutomationControlled");
            options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
            options.setExperimentalOption("useAutomationExtension", false);

            driver = new ChromeDriver(options);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // 2. Navigate to Amazon
            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.amazon.in/s?k=" + encodedQuery;
            driver.get(url);

            // 3. Wait for products to load
            String wrapperSelector = SelectorConfig.get("amazon", "product_wrapper");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(wrapperSelector)));

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
