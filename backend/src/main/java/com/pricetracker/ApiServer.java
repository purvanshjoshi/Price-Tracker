package com.pricetracker;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.before;
import static spark.Spark.options;

import com.google.gson.Gson;
import com.pricetracker.engine.EngineManager;
import com.pricetracker.model.Product;
import java.util.Collection;

public class ApiServer {

    private static final Gson gson = new Gson();
    private static final EngineManager engine = new EngineManager();

    public static void start() {
        // Hugging Face Spaces use port 7860 by default
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "7860"));
        port(port);

        // Enable CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.type("application/json");
        });

        // Search Endpoint: GET /api/search?q=iphone
        get("/api/search", (req, res) -> {
            String query = req.queryParams("q");
            if (query == null || query.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Search query 'q' is required."));
            }

            // Security Sanitization for CodeQL
            String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
            if (sanitizedQuery.length() > 100) {
                sanitizedQuery = sanitizedQuery.substring(0, 100);
            }

            System.out.println("Cloud API: Searching for " + sanitizedQuery);
            
            // Trigger our existing EngineManager logic
            Collection<Product> results = engine.executeSearch(sanitizedQuery).values();
            
            return gson.toJson(results);
        });

        // History Endpoint: GET /api/history?title=...
        get("/api/history", (req, res) -> {
            String title = req.queryParams("title");
            if (title == null || title.isEmpty()) {
                res.status(400);
                return gson.toJson(new ErrorResponse("Product title is required."));
            }

            System.out.println("Cloud API: Fetching history for " + title);
            
            // We'll update PriceHistoryDAO to support this
            com.pricetracker.engine.PriceHistoryDAO dao = new com.pricetracker.engine.PriceHistoryDAO();
            return gson.toJson(dao.getHistory(title));
        });

        System.out.println("Price Scout Cloud API started on port: " + port);
    }

    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }
}
