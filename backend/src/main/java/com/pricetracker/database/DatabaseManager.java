package com.pricetracker.database;

import com.pricetracker.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    // SQLite uses a simple file on your disk - no server or password needed!
    private static final String DB_URL = "jdbc:sqlite:price_history.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        // This will automatically create the database file and table if they don't exist
        String createTableSQL = "CREATE TABLE IF NOT EXISTS price_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "product_name TEXT NOT NULL," +
                "platform TEXT NOT NULL," +
                "price REAL NOT NULL," +
                "url TEXT," +
                "scraped_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.err.println("SQLite Database initialized at backend/price_history.db");
        } catch (SQLException e) {
            System.err.println("Error initializing SQLite: " + e.getMessage());
        }
    }

    public void savePriceHistory(Product product) {
        String sql = "INSERT INTO price_history(product_name, platform, price, url) VALUES(?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getPlatform());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getUrl());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (SQLite): " + e.getMessage());
        }
    }
}
