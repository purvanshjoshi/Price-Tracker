package com.pricetracker;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Price Scout Cloud Engine Launching ---");
        
        // Start the Web API Server
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
