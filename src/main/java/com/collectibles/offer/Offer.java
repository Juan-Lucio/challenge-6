package com.collectibles.offer;

/**
 * Model (POJO) representing a single offer made by a user
 * on a specific item.
 */
public class Offer {

    private String name;
    private String email;
    private String itemId;
    private double amount;

    // Constructor to create a new offer
    public Offer(String name, String email, String itemId, double amount) {
        this.name = name;
        this.email = email;
        this.itemId = itemId;
        this.amount = amount;
    }

    // Getters are required for Mustache templates to read the properties
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getItemId() {
        return itemId;
    }

    public double getAmount() {
        return amount;
    }
}