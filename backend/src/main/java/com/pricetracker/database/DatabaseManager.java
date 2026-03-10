package com.pricetracker.database;

import com.pricetracker.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    
    // Remember to update these with your actual MySQL credentials if different!
    private static final String URL = "jdbc:mysql://localhost:3306/price_tracker_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // Change to your password 

    /**
     * Attempts to establish a connection to the MySQL database.
     */
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Saves a scraped product into the price_history table.
     */
    public void savePriceHistory(Product product) {
        String query = "INSERT INTO price_history(product_name, platform, price, url) VALUES(?,?,?,?)";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getPlatform());
            pstmt.setDouble(3, product.getPrice());
            pstmt.setString(4, product.getUrl());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            // We print to System.err so it doesn't corrupt our
            // Chrome Native Messaging JSON on System.out
            System.err.println("Database Error saving product: " + e.getMessage());
        }
    }
}
