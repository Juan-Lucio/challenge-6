package com.collectibles.item;

import com.collectibles.utils.JsonUtil;
import com.google.gson.reflect.TypeToken;
import org.jdbi.v3.core.Jdbi;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import java.util.Optional;

/**
 * ItemService refactored to use a Jdbi database connection
 * instead of an in-memory Map.
 */
public class ItemService {

    private final Jdbi jdbi;

    public ItemService(Jdbi jdbi) {
        this.jdbi = jdbi;
        
        // Populate the database from items.json if it's empty
        seedDatabaseIfEmpty();
    }

    /**
     * SPRINT 3: Filter logic is now executed by the database.
     */
    public List<Item> getAllItems(String minPriceStr, String maxPriceStr) {
        double minPrice = parseDouble(minPriceStr, 0.0);
        double maxPrice = parseDouble(maxPriceStr, Double.MAX_VALUE);

        // Jdbi executes the SQL query
        return jdbi.withHandle(handle -> 
            handle.createQuery(
                "SELECT * FROM items WHERE price >= :min AND price <= :max ORDER BY name")
                .bind("min", minPrice)
                .bind("max", maxPrice)
                .mapToBean(Item.class) // Maps columns to Item setters
                .list()
        );
    }

    public Optional<Item> getItemById(String id) {
        return jdbi.withHandle(handle -> 
            handle.createQuery("SELECT * FROM items WHERE id = :id")
                .bind("id", id)
                .mapToBean(Item.class)
                .findFirst() // Returns Optional<Item>
        );
    }

    /**
     * SPRINT 3: This now updates the persistent database.
     */
    public boolean updateItemPrice(String itemId, double newPrice) {
        int rowsUpdated = jdbi.withHandle(handle ->
            handle.createUpdate("UPDATE items SET price = :price WHERE id = :id")
                .bind("price", newPrice)
                .bind("id", itemId)
                .execute()
        );
        return rowsUpdated > 0;
    }
    
    /**
     * Helper to seed the database from items.json on first launch.
     */
    private void seedDatabaseIfEmpty() {
        boolean isEmpty = jdbi.withHandle(handle -> 
            handle.createQuery("SELECT COUNT(*) FROM items")
                  .mapTo(Integer.class)
                  .one()
        ) == 0;

        if (isEmpty) {
            System.out.println("Database is empty. Seeding items from items.json...");
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("items.json")) {
                if (is == null) throw new RuntimeException("items.json not found");
                
                InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                Type itemListType = new TypeToken<List<Item>>(){}.getType();
                List<Item> items = JsonUtil.fromJson(reader, itemListType);

                // Insert each item into the database
                jdbi.useHandle(handle -> {
                    for (Item item : items) {
                        handle.createUpdate("INSERT INTO items (id, name, description, price, imageUrl) " +
                                            "VALUES (:id, :name, :description, :price, :imageUrl)")
                            .bindBean(item) // Binds the Item object to the query
                            .execute();
                    }
                });
                System.out.println("Database seeded with " + items.size() + " items.");
            } catch (Exception e) {
                System.err.println("Failed to seed database: " + e.getMessage());
            }
        }
    }

    // --- (Métodos del Sprint 1 para la API antigua, si aún se necesitan) ---
    // (Por ahora los omitimos para mantener la limpieza,
    // ya que nuestra app JS ya no los usa)
    
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
}