/*
 * Defines the database schema for the Collector's Vault project.
 */

-- Table for Items (Products)
CREATE TABLE IF NOT EXISTS items (
    id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    /* --- CORRECCIÓN: Usamos NUMERIC para precisión arbitraria --- */
    price NUMERIC NOT NULL, 
    imageUrl TEXT
);

-- Table for Offers (Bids)
CREATE TABLE IF NOT EXISTS offers (
    offer_id SERIAL PRIMARY KEY,
    item_id VARCHAR(50) NOT NULL REFERENCES items(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    /* --- CORRECCIÓN: Usamos NUMERIC para precisión arbitraria --- */
    amount NUMERIC NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Create an index on itemId for faster offer lookups
CREATE INDEX IF NOT EXISTS idx_offers_item_id ON offers(item_id);