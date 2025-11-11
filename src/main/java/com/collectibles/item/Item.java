package com.collectibles.item;

/**
 * Model (POJO) for an Item.
 * Refactored to be a "Java Bean" with getters, setters,
 * and a no-arg constructor for Jdbi mapping.
 */
public class Item {
    private String id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;

    // No-arg constructor required by Jdbi
    public Item() {}
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    
    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}