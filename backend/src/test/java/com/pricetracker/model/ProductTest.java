package com.pricetracker.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {

    @Test
    void constructorSetsFields() {
        Product p = new Product("iPhone 15", 79999.0, "Amazon", "https://amazon.in/dp/xyz");
        assertEquals("iPhone 15", p.getName());
        assertEquals(79999.0, p.getPrice());
        assertEquals("Amazon", p.getPlatform());
        assertEquals("https://amazon.in/dp/xyz", p.getUrl());
    }

    @Test
    void compareToSortsByPriceAscending() {
        Product cheap = new Product("A", 100.0, "S1", "u1");
        Product expensive = new Product("B", 200.0, "S2", "u2");
        assertTrue(cheap.compareTo(expensive) < 0);
        assertTrue(expensive.compareTo(cheap) > 0);
        assertEquals(0, cheap.compareTo(cheap));
    }

    @Test
    void toJSONContainsAllFields() {
        Product p = new Product("Test Product", 500.0, "Flipkart", "https://flipkart.com/p");
        var json = p.toJSON();
        assertEquals("Test Product", json.getString("name"));
        assertEquals(500.0, json.getDouble("price"));
        assertEquals("Flipkart", json.getString("platform"));
        assertEquals("https://flipkart.com/p", json.getString("url"));
    }

    @Test
    void toStringContainsPlatformNameAndPrice() {
        Product p = new Product("Pixel 8", 69999.0, "Croma", "https://croma.com/p");
        String s = p.toString();
        assertTrue(s.contains("Croma"));
        assertTrue(s.contains("Pixel 8"));
        assertTrue(s.contains("69999.0"));
    }
}
