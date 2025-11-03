package com.collectibles.item;

public class Item {
    private String id;
    private String name;
    private String description;
    private String price;
    // --- NUEVO CAMPO ---
    private String imageUrl;

    // Getters for Mustache template to access properties
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    // --- NUEVO GETTER ---
    public String getImageUrl() {
        return imageUrl;
    }
}