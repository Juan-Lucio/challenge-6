package com.collectibles.item;

import static spark.Spark.*;
import com.collectibles.utils.JsonUtil;

public class ItemController {

    private final ItemService itemService;

    // <<< CORRECCIÓN AQUÍ >>>
    // El constructor ahora solo guarda el servicio.
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * <<< CORRECIÓN AQUÍ >>>
     * Este es el nuevo método público que App.java llamará
     * DESPUÉS de haber creado el grupo /api.
     */
    public void registerRoutes() {
        
        // Grupo de rutas para /items (será /api/items)
        path("/items", () -> {

            // GET /api/items
            get("", (req, res) -> {
                return itemService.getAllItems();
            }, JsonUtil::toJson);

            // GET /api/items/:id/description
            get("/:id/description", (req, res) -> {
                String id = req.params(":id");
                
                return itemService.getItemDescription(id)
                        .orElse(null);
                
            }, JsonUtil::toJson);
        });
    }
}