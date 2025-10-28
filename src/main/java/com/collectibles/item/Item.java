package com.collectibles.item;

// Este es un POJO (Plain Old Java Object).
// Sus atributos coinciden con las claves del items.json.
// Gson usará esta clase para convertir el JSON a un objeto Java.
public class Item {
    private String id;
    private String name;
    private String description;
    private String price;

    // Constructor vacío (buena práctica para Gson)
    public Item() {}

    // Getters para que Gson pueda leer los datos
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
}