package com.pricetracker.engine;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

/**
 * Utility class for resilient HTTP connections.
 * This version uses Mobile User-Agents and human-like delays to bypass blocks.
 */
public class HttpUtils {

    private static final String[] DESKTOP_USER_AGENTS = {
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:121.0) Gecko/20100101 Firefox/121.0"
    };

    private static final Random RANDOM = new Random();
    private static final Map<String, Map<String, String>> SESSION_COOKIES = new HashMap<>();

    public static Connection connect(String url) {
        // SSRF Validation for CodeQL
        if (url == null || (!url.startsWith("https://www.amazon.in") && 
                           !url.startsWith("https://www.flipkart.com") &&
                           !url.startsWith("https://www.reliancedigital.in") &&
                           !url.startsWith("https://www.croma.com"))) {
            throw new IllegalArgumentException("Unauthorized URL domain: " + url);
        }

        String baseDomain = url.contains("amazon") ? "https://www.amazon.in" : "https://www.flipkart.com";
        String userAgent = DESKTOP_USER_AGENTS[RANDOM.nextInt(DESKTOP_USER_AGENTS.length)];
        
        // 1. Random delay to mimic human behavior (2-4 seconds)
        try {
            Thread.sleep(2000 + RANDOM.nextInt(2000));
        } catch (InterruptedException ignored) {}

        // 2. Fetch session cookies if not already present for this domain
        if (!SESSION_COOKIES.containsKey(baseDomain)) {
            try {
                // Use a very simple connection for the home page pre-hit
                Connection.Response res = Jsoup.connect(baseDomain)
                    .userAgent(userAgent)
                    .timeout(10000)
                    .execute();
                SESSION_COOKIES.put(baseDomain, res.cookies());
            } catch (IOException e) {
                System.err.println("[HttpUtils] Warning: Pre-hit failed: " + e.getMessage());
            }
        }

        return Jsoup.connect(url)
            .userAgent(userAgent)
            .cookies(SESSION_COOKIES.getOrDefault(baseDomain, new HashMap<>()))
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .header("Accept-Language", "en-US,en;q=0.5")
            .timeout(20000)
            .followRedirects(true)
            .ignoreHttpErrors(true); // Let us see the body even if it's 503
    }
}
