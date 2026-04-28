package com.pricetracker.engine;

import com.pricetracker.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PriceHistoryDAO {
    private static final String DB_URL = "jdbc:sqlite:price_history.db";

    public PriceHistoryDAO() {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS history (" +
                             "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                             "title TEXT NOT NULL," +
                             "price REAL NOT NULL," +
                             "platform TEXT NOT NULL," +
                             "url TEXT," +
                             "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP" +
                             ");";
                Statement stmt = conn.createStatement();
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            System.err.println("DB Initialization Error: " + e.getMessage());
        }
    }

    public List<Product> getHistory(String title) {
        List<Product> history = new ArrayList<>();
        String sql = "SELECT title, price, platform, url FROM history WHERE title LIKE ? ORDER BY timestamp DESC LIMIT 50";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                history.add(new Product(
                    rs.getString("title"),
                    rs.getDouble("price"),
                    rs.getString("platform"),
                    rs.getString("url")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Query Error: " + e.getMessage());
        }
        return history;
    }
}
