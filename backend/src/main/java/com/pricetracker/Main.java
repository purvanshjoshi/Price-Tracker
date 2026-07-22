package com.pricetracker;

import com.pricetracker.database.MigrationRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOG.info("Price Scout Cloud Engine launching");

        MigrationRunner.migrate();

        ApiServer.start();
    }

    // Basic validation to reject malformed or suspicious search input
    private static boolean isValidSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return false;
        }

        if (query.length() > 100) {
            return false;
        }

        String lowerQuery = query.toLowerCase();

        return !lowerQuery.contains(";")
                && !lowerQuery.contains("--")
                && !lowerQuery.contains("/*")
                && !lowerQuery.contains("*/");
    }
}
