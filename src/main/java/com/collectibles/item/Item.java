package com.collectibles.item;

public class Item {
    private String id;
    private String name;
    private String description;
    
    // --- CAMBIO DE TIPO DE DATO ---
    private double price;
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

    // --- GETTER ACTUALIZADO ---
    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // --- NUEVO SETTER (Para S3.2) ---
    // This allows our service to update the price in memory
    public void setPrice(double price) {
        this.price = price;
    }
}