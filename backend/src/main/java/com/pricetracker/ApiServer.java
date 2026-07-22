package com.pricetracker;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.exception;
import static spark.Spark.notFound;

import com.google.gson.Gson;
import com.pricetracker.engine.EngineManager;
import com.pricetracker.model.Product;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiServer {

    private static final Logger LOG = LoggerFactory.getLogger(ApiServer.class);
    private static final Gson GSON = new Gson();
    private static final EngineManager ENGINE = new EngineManager();

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

        // Global error handler for unhandled exceptions
        exception(Exception.class, (exc, req, res) -> {
            res.status(500);
            res.body(GSON.toJson(new ErrorResponse("Internal server error")));
        });

        // 404 handler for unmatched routes
        notFound((req, res) -> {
            res.type("application/json");
            return GSON.toJson(new ErrorResponse("Not found"));
        });

        // Health Endpoint: GET /api/health
        get("/api/health", (req, res) -> {
            res.status(200);
            return GSON.toJson(new HealthResponse("ok"));
        });

        // Search Endpoint: GET /api/search?q=iphone
        get("/api/search", (req, res) -> {
            String query = req.queryParams("q");
            if (query == null || query.isEmpty()) {
                res.status(400);
                return GSON.toJson(new ErrorResponse("Search query 'q' is required."));
            }

            // Security Sanitization for CodeQL
            String sanitizedQuery = query.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
            if (sanitizedQuery.length() > 100) {
                sanitizedQuery = sanitizedQuery.substring(0, 100);
            }

            LOG.info("Searching for {}", sanitizedQuery);
            
            Collection<Product> results = ENGINE.executeSearch(sanitizedQuery).values();
            
            return GSON.toJson(results);
        });

        // History Endpoint: GET /api/history?title=...
        get("/api/history", (req, res) -> {
            String title = req.queryParams("title");
            if (title == null || title.isEmpty()) {
                res.status(400);
                return GSON.toJson(new ErrorResponse("Product title is required."));
            }

            LOG.info("Fetching history for {}", title);
            
            com.pricetracker.engine.PriceHistoryDAO dao = new com.pricetracker.engine.PriceHistoryDAO();
            return GSON.toJson(dao.getHistory(title));
        });

        LOG.info("Price Scout Cloud API started on port {}", port);
    }

    private static class ErrorResponse {
        String error;
        ErrorResponse(String error) { this.error = error; }
    }

    private static class HealthResponse {
        String status;
        HealthResponse(String status) { this.status = status; }
    }
}
