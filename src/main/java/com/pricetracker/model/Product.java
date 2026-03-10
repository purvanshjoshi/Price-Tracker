package com.pricetracker.model;

import org.json.JSONObject;

public class Product implements Comparable<Product> {
    private String name;
    private double price;
    private String platform;
    private String url;

    public Product(String name, double price, String platform, String url) {
        this.name = name;
        this.price = price;
        this.platform = platform;
        this.url = url;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getPlatform() { return platform; }
    public String getUrl() { return url; }

    // This converts the Java object neatly into a JSON object 
    // so Chrome can understand it later.
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("price", price);
        json.put("platform", platform);
        json.put("url", url);
        return json;
    }

    // Default sorting logic: sort by cheapest price first
    @Override
    public int compareTo(Product other) {
        return Double.compare(this.price, other.price);
    }

    @Override
    public String toString() {
        return platform + ": " + name + " - ₹" + price;
    }
}
