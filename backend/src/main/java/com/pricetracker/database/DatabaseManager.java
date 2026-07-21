package com.pricetracker.database;

import com.pricetracker.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:price_history.db";

    public DatabaseManager() {
        // Schema is managed by MigrationRunner
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
