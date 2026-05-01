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
            // 1. Setup Stealth Driver
            driver = com.pricetracker.engine.WebDriverFactory.createStealthDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

            String encodedQuery = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String url = "https://www.reliancedigital.in/search?q=" + encodedQuery;
            driver.get(url);

            // 2. Diagnostics on failure
            String wrapperSelector = SelectorConfig.get("reliance", "product_wrapper");
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(wrapperSelector)));
            } catch (Exception e) {
                System.err.println("[Reliance] Failed to find results. Page Title: " + driver.getTitle());
                System.err.println("[Reliance] URL: " + driver.getCurrentUrl());
                throw e;
            }

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
