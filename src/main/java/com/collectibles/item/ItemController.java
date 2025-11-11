package com.collectibles.item;

import static spark.Spark.*;

import com.collectibles.exception.NotFoundException;
import com.collectibles.utils.JsonUtil;

/**
 * ItemController (Refactored for "Big Bang")
 * This controller now only supports the routes
 * required by our client-side JavaScript application.
 */
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    public void registerRoutes() {
        
        path("/items", () -> {

            // GET /api/items/:id (Used by item-detail-app.js)
            get("/:id", (req, res) -> {
                String id = req.params(":id");
                
                // This method (getItemById) *does* exist in our new Jdbi service
                return itemService.getItemById(id)
                    .orElseThrow(() -> new NotFoundException("API: Item not found"));
                
            }, JsonUtil::toJson);
            
            /*
             * The old routes (GET /api/items and GET /api/items/:id/description)
             * have been removed because they are no longer used by our
             * refactored frontend and the methods were deleted from ItemService.
             */
        });
    }
}