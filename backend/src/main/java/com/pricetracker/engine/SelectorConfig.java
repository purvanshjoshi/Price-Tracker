package com.pricetracker.engine;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

public class SelectorConfig {
    private static JSONObject config;
    private static final String CONFIG_FILE = "selectors.json";

    /**
     * Initializes the config by reading the json file from the current directory.
     */
    static {
        load();
    }

    public static void load() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(CONFIG_FILE)));
            config = new JSONObject(content);
            System.err.println("[SelectorConfig] Successfully loaded " + CONFIG_FILE);
        } catch (IOException e) {
            System.err.println("[SelectorConfig] FATAL: Could not read " + CONFIG_FILE + ". Error: " + e.getMessage());
            // Fallback to empty JSON to avoid NullPointerException
            config = new JSONObject();
        }
    }

    /**
     * Retrieves a selector for a specific site and field.
     */
    public static String get(String site, String field) {
        if (config.has(site)) {
            JSONObject siteConfig = config.getJSONObject(site);
            if (siteConfig.has(field)) {
                return siteConfig.getString(field);
            }
        }
        return "";
    }
}
