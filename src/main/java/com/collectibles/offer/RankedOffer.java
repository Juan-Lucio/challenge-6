package com.collectibles.offer;

// Imports para formatear moneda
import java.text.NumberFormat;
import java.util.Locale;

public class RankedOffer {

    private String itemName;
    private double amount;
    private String name;
    private String email;

    public RankedOffer() {}

    // Getters para Jdbi
    public double getAmount() { return amount; }
    public String getItemName() { return itemName; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // --- (NUEVO GETTER DE FORMATO PARA MUSTACHE) ---
    public String getFormattedAmount() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        return currencyFormat.format(this.amount);
    }

    // Setters para Jdbi
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}