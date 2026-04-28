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

public class RelianceScraper implements ScraperCallable {

    private final String searchKeyword;

    public RelianceScraper(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    @Override
    public Product scrape(String keyword) {
        WebDriver driver = null;
        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless=new");
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
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.reliancedigital.in/search?q=" + encodedQuery;
            driver.get(url);

            // Scroll down to trigger lazy loading
            try {
                org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
                js.executeScript("window.scrollBy(0,500)");
                Thread.sleep(2000);
            } catch (Exception e) {
                // Ignore scroll error
            }

            String wrapperSelector = SelectorConfig.get("reliance", "product_wrapper");
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(wrapperSelector)));

            WebElement firstResult = driver.findElement(By.cssSelector(wrapperSelector));
            
            if (firstResult != null) {
                String title = firstResult.findElement(By.cssSelector(SelectorConfig.get("reliance", "title"))).getText();
                
                double price = Double.MAX_VALUE;
                try {
                    String priceText = firstResult.findElement(By.cssSelector(SelectorConfig.get("reliance", "price"))).getText().replaceAll("[^0-9.]", "");
                    if (!priceText.isEmpty()) {
                        price = Double.parseDouble(priceText);
                    }
                } catch (Exception e) {
                    // Ignore price error
                }

                String productLink = url;
                try {
                    productLink = firstResult.findElement(By.cssSelector(SelectorConfig.get("reliance", "link"))).getAttribute("href");
                } catch (Exception e) {
                    // Ignore link error
                }

                return new Product(title, price, "Reliance Digital", productLink);
            }

        } catch (Exception e) {
            System.err.println("Reliance Selenium Scraper Failed: " + e.getMessage());
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return new Product(keyword + " (Not Found)", Double.MAX_VALUE, "Reliance Digital", "");
    }

    @Override
    public Product call() throws Exception {
        return scrape(searchKeyword);
    }
}
