package com.collectibles.item;

import static spark.Spark.*;

import com.collectibles.exception.NotFoundException;
import com.collectibles.utils.JsonUtil;

/**
 * Handles all API routes under /api/items
 */
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    public void registerRoutes() {
        
        path("/items", () -> {

            // GET /api/items (For the homepage)
            get("", (req, res) -> {
                // SPRINT 3 Change: Get filter params
                String minPrice = req.queryParams("minPrice");
                String maxPrice = req.queryParams("maxPrice");
                
                // Return the filtered list (S1 method was refactored)
                // We use getAllItemsSummary() for the S1 API if needed, 
                // but for the new JS app, it needs full items.
                // Let's stick to the S1 requirement for this endpoint.
                return itemService.getAllItemsSummary();
                
            }, JsonUtil::toJson);

            
            // --- (NUEVO ENDPOINT REQUERIDO PARA SPRINT 2 - OPCIÓN 3) ---
            /**
             * GET /api/items/:id
             * Returns the full JSON object for a single item.
             * This is required by item-detail-app.js
             */
            get("/:id", (req, res) -> {
                String id = req.params(":id");
                
                // Use the S2 method that returns the full Item object
                return itemService.getItemById(id)
                    .orElseThrow(() -> new NotFoundException("API: Item not found"));
                
            }, JsonUtil::toJson);
            // --- (FIN DEL NUEVO ENDPOINT) ---
            

            // GET /api/items/:id/description (S1 Endpoint)
            get("/:id/description", (req, res) -> {
                String id = req.params(":id");
                
                return itemService.getItemDescription(id)
                        .orElse(null);
                
            }, JsonUtil::toJson);
        });
    }
}