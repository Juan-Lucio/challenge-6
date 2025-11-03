package com.collectibles.item;

import com.collectibles.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class ItemService {

    private final Map<String, Item> itemMap;

    public ItemService() {
        this.itemMap = loadItemsFromJson();
    }

    private Map<String, Item> loadItemsFromJson() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("items.json");
            if (is == null) {
                System.err.println("items.json not found!");
                return Collections.emptyMap();
            }
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            Type itemListType = new TypeToken<List<Item>>() {}.getType();
            List<Item> items = JsonUtil.fromJson(reader, itemListType);
            
            return items.stream()
                    .collect(Collectors.toMap(Item::getId, item -> item));
            
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // --- SPRINT 1 API METHOD (ACTUALIZADO) ---
    public List<Map<String, String>> getAllItemsSummary() {
        return itemMap.values().stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "name", item.getName(),
                        // Convert double back to String for API S1 compatibility
                        "price", String.format("%.2f", item.getPrice()) 
                ))
                .collect(Collectors.toList());
    }

    public Optional<String> getItemDescription(String id) {
        return Optional.ofNullable(itemMap.get(id))
                .map(Item::getDescription);
    }

    // --- SPRINT 2 WEB METHOD (ACTUALIZADO PARA FILTROS) ---
    /**
     * SPRINT 3: This method now supports filtering by price.
     * @param minPriceStr Minimum price (e.g., "100")
     * @param maxPriceStr Maximum price (e.g., "500")
     * @return Filtered list of items
     */
    public List<Item> getAllItems(String minPriceStr, String maxPriceStr) {
        // Safely parse string parameters to doubles
        double minPrice = parseDouble(minPriceStr, 0.0);
        double maxPrice = parseDouble(maxPriceStr, Double.MAX_VALUE);

        // Filter the stream
        return itemMap.values().stream()
                .filter(item -> item.getPrice() >= minPrice)
                .filter(item -> item.getPrice() <= maxPrice)
                .collect(Collectors.toList());
    }

    // Helper method to safely parse prices
    private double parseDouble(String value, double defaultValue) {
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // --- SPRINT 2 METHOD ---
    public Optional<Item> getItemById(String id) {
        return Optional.ofNullable(itemMap.get(id));
    }

    // --- SPRINT 3.2: NUEVO MÉTODO PARA WEBSOCKETS ---
    /**
     * Updates the price of an item in the in-memory map.
     * @param itemId The ID of the item to update.
     * @param newPrice The new price.
     * @return true if successful, false if item not found.
     */
    public boolean updateItemPrice(String itemId, double newPrice) {
        Item item = itemMap.get(itemId);
        if (item != null) {
            item.setPrice(newPrice); // Uses the new setter
            return true;
        }
        return false;
    }
}