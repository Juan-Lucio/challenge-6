package com.collectibles.item;

import static spark.Spark.*;
import com.collectibles.utils.JsonUtil;

/**
 * Handles all API routes under /api/items
 */
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        // Constructor now only stores the service
        this.itemService = itemService;
    }

    /**
     * This public method is called by App.java
     * to register all routes for this controller *inside* the /api path.
     */
    public void registerRoutes() {
        
        path("/items", () -> {

            // GET /api/items
            // (Uses the S1 method)
            get("", (req, res) -> {
                return itemService.getAllItemsSummary();
            }, JsonUtil::toJson);

            // GET /api/items/:id/description
            // (Uses the S1 method)
            get("/:id/description", (req, res) -> {
                String id = req.params(":id");
                
                return itemService.getItemDescription(id)
                        .orElse(null);
                
            }, JsonUtil::toJson);
        });
    }
}