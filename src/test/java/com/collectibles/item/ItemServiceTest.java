package com.collectibles.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ItemService.
 * This class tests the in-memory filtering logic and price updates.
 * It relies on the real items.json file being present in resources.
 */
class ItemServiceTest {

    private ItemService itemService;

    @BeforeEach
    void setUp() {
        // Arrange: Create a new service. This loads items.json
        itemService = new ItemService();
    }

    @Test
    @DisplayName("Should return all items when filters are null or empty")
    void testGetAllItemsNoFilters() {
        // Act
        List<Item> items = itemService.getAllItems(null, null);
        // Assert
        assertEquals(7, items.size()); // We know there are 7 items in items.json

        // Act
        List<Item> itemsEmpty = itemService.getAllItems("", "");
        // Assert
        assertEquals(7, itemsEmpty.size());
    }

    @Test
    @DisplayName("Should filter by minimum price")
    void testGetAllItemsWithMinPrice() {
        // Act: Get items >= $700
        List<Item> items = itemService.getAllItems("700", null);
        
        // Assert: Should find 2 items (Rosalia: 734.57, Delgadillo: 823.12)
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(item -> item.getPrice() >= 700));
    }

    @Test
    @DisplayName("Should filter by maximum price")
    void testGetAllItemsWithMaxPrice() {
        // Act: Get items <= $500
        List<Item> items = itemService.getAllItems(null, "500");
        
        // Assert: Should find 2 items (Coldplay: 458.91, Snoop Dogg: 355.67)
        assertEquals(2, items.size());
        assertTrue(items.stream().allMatch(item -> item.getPrice() <= 500));
    }

    @Test
    @DisplayName("Should filter by min and max price")
    void testGetAllItemsWithRange() {
        // Act: Get items >= $500 and <= $700
        List<Item> items = itemService.getAllItems("500", "700");
        
        // Assert: Should find 3 items (Peso Pluma: 621.34, Bad Bunny: 521.89, Cardi B: 674.23)
        assertEquals(3, items.size());
        assertTrue(items.stream().allMatch(item -> item.getPrice() >= 500 && item.getPrice() <= 700));
    }

    @Test
    @DisplayName("Should return empty list for no matches")
    void testGetItemsNoMatches() {
        // Act: Get items >= $9000
        List<Item> items = itemService.getAllItems("9000", null);
        
        // Assert
        assertNotNull(items);
        assertTrue(items.isEmpty());
    }

    @Test
    @DisplayName("Should correctly update an item's price")
    void testUpdateItemPrice() {
        // Arrange
        String itemId = "item1"; // Rosalía's Helmet ($734.57)
        double newPrice = 999.99;

        // Act: Update the price
        boolean success = itemService.updateItemPrice(itemId, newPrice);

        // Assert: Check that the update was successful
        assertTrue(success);
        
        // Assert: Verify the new price is stored in memory
        Optional<Item> updatedItem = itemService.getItemById(itemId);
        assertTrue(updatedItem.isPresent());
        assertEquals(newPrice, updatedItem.get().getPrice());
    }

    @Test
    @DisplayName("Should fail to update price for non-existent item")
    void testUpdatePriceNonExistentItem() {
        // Act
        boolean success = itemService.updateItemPrice("fake-id", 999.00);
        
        // Assert
        assertFalse(success);
    }
}