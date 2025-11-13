package com.collectibles.offer;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Model (POJO) for an Offer.
 * Refactored to be a "Java Bean" for Jdbi mapping.
 */
public class Offer {

    private int offer_id;
    private String item_id;
    private String name;
    private String email;
    private double amount;
    private Timestamp created_at;

    /** No-arg constructor required by Jdbi */
    public Offer() {}
    
    /** Constructor for creating new offers */
    public Offer(String name, String email, String itemId, double amount) {
        this.name = name;
        this.email = email;
        this.item_id = itemId;
        this.amount = amount;
    }

    /**
     * Formats the 'amount' double into a proper currency String for Mustache.
     * (e.g., 501.0 -> $501.00)
     */
    public String getFormattedAmount() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(this.amount);
    }

    // Getters
    public int getOffer_id() { return offer_id; }
    public String getItem_id() { return item_id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public double getAmount() { return amount; }
    public Timestamp getCreated_at() { return created_at; }
    
    // Setters
    public void setOffer_id(int offer_id) { this.offer_id = offer_id; }
    public void setItem_id(String item_id) { this.item_id = item_id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCreated_at(Timestamp created_at) { this.created_at = created_at; }
}