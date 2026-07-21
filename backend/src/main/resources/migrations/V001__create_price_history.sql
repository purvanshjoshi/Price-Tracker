CREATE TABLE IF NOT EXISTS price_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    product_name TEXT NOT NULL,
    platform TEXT NOT NULL,
    price REAL NOT NULL,
    url TEXT,
    scraped_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_price_history_product_name ON price_history(product_name);
