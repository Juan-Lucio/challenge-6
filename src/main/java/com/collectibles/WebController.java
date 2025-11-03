package com.collectibles;

import com.collectibles.exception.NotFoundException;
import com.collectibles.item.Item;
import com.collectibles.item.ItemService;
import com.collectibles.offer.Offer;
import com.collectibles.offer.OfferService;
// --- ¡CORRECCIÓN AQUÍ! SE AÑADIÓ ESTE IMPORT ---
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
// ---
import spark.ModelAndView;
import spark.TemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static spark.Spark.*;

public class WebController {

    private final ItemService itemService;
    private final OfferService offerService;
    private final TemplateEngine templateEngine;

    public WebController(ItemService itemService, OfferService offerService, TemplateEngine templateEngine) {
        this.itemService = itemService;
        this.offerService = offerService;
        this.templateEngine = templateEngine;
    }

    public void registerRoutes() {

        // --- (GET / ACTUALIZADO PARA SPRINT 3.1) ---
        get("/", (req, res) -> {
            String minPrice = req.queryParams("minPrice");
            String maxPrice = req.queryParams("maxPrice");

            List<Item> items = itemService.getAllItems(minPrice, maxPrice);
            
            Map<String, Object> model = new HashMap<>();
            model.put("items", items);
            
            model.put("minPrice", minPrice);
            model.put("maxPrice", maxPrice);

            return templateEngine.render(new ModelAndView(model, "index.mustache"));
        });

        // --- (GET /:id sin cambios) ---
        get("/:id", (req, res) -> {
            String id = req.params(":id");
            Optional<Item> itemOpt = itemService.getItemById(id);

            if (itemOpt.isEmpty()) {
                throw new NotFoundException("Item with ID '" + id + "' not found.");
            }

            List<Offer> offers = offerService.getOffersByItemId(id);
            
            Map<String, Object> model = new HashMap<>();
            model.put("item", itemOpt.get());
            model.put("offers", offers);

            return templateEngine.render(new ModelAndView(model, "item.mustache"));
        });

        // --- (POST /:id/offer ACTUALIZADO PARA SPRINT 3.2) ---
        post("/:id/offer", (req, res) -> {
            String id = req.params(":id");
            String bidderName = req.queryParams("bidderName");
            String bidderEmail = req.queryParams("bidderEmail");
            
            double offerAmount = 0.0;
            try {
                offerAmount = Double.parseDouble(req.queryParams("offerAmount"));
            } catch (NumberFormatException e) {
                res.redirect("/" + id);
                return null;
            }

            Offer newOffer = new Offer(bidderName, bidderEmail, id, offerAmount);
            offerService.addOffer(newOffer);
            
            System.out.println("[OFFER SAVED] Item ID: " + id + 
                               ", Bidder: " + bidderName + 
                               ", Amount: $" + offerAmount);

            // --- LÓGICA DE WEBSOCKET (SPRINT 3.2) ---
            boolean updated = itemService.updateItemPrice(id, offerAmount);
            if (updated) {
                // Esta es la línea que fallaba (ahora funciona gracias al import)
                PriceUpdateWebSocketHandler.broadcastPriceUpdate(id, offerAmount);
            }
            // --- FIN LÓGICA WEBSOCKET ---

            res.redirect("/" + id);
            return null;
        });
    }
}