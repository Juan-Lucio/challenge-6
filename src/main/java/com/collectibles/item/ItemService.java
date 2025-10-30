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

    // Use a Map for fast O(1) lookups by ID
    private final Map<String, Item> itemMap;

    public ItemService() {
        // Load data from JSON file on service initialization
        this.itemMap = loadItemsFromJson();
    }

    private Map<String, Item> loadItemsFromJson() {
        try {
            // Read file from src/main/resources
            InputStream is = getClass().getClassLoader().getResourceAsStream("items.json");
            if (is == null) {
                System.err.println("items.json not found!");
                return Collections.emptyMap();
            }
            
            InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
            
            // Define the list type for Gson
            Type itemListType = new TypeToken<List<Item>>(){}.getType();
            List<Item> items = JsonUtil.fromJson(reader, itemListType);
            
            // Convert the List to a Map (id -> Item) for fast lookups
            return items.stream()
                    .collect(Collectors.toMap(Item::getId, item -> item));
            
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    // --- SPRINT 1 API METHODS ---

    /**
     * SPRINT 1 API METHOD
     * Returns a partial list (Map) for the API.
     */
    public List<Map<String, String>> getAllItemsSummary() {
        return itemMap.values().stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "name", item.getName(),
                        "price", item.getPrice()
                ))
                .collect(Collectors.toList());
    }

    /**
     * SPRINT 1 API METHOD
     * Returns just the description for the API.
     */
    public Optional<String> getItemDescription(String id) {
        return Optional.ofNullable(itemMap.get(id))
                .map(Item::getDescription);
    }

    // --- SPRINT 2 WEB METHODS ---

    /**
     * SPRINT 2 WEB METHOD
     * Returns a complete list of Item objects for the website views.
     * @return List<Item>
     */
    public List<Item> getAllItems() {
        return new ArrayList<>(itemMap.values());
    }

    /**
     * SPRINT 2 WEB METHOD
     * Finds a single Item by its ID for the item detail page.
     * @param id The item ID.
     * @return Optional<Item>
     */
    public Optional<Item> getItemById(String id) {
        return Optional.ofNullable(itemMap.get(id));
    }
}