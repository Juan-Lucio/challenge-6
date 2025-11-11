package com.collectibles;

import com.collectibles.exception.NotFoundException;
// --- ¡LA CORRECCIÓN ESTÁ AQUÍ! ---
import com.collectibles.item.Item; 
import com.collectibles.item.ItemService;
import com.collectibles.offer.Offer;
import com.collectibles.offer.OfferService;
import com.collectibles.offer.RankedOffer;
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
import spark.ModelAndView;
import spark.TemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        /**
         * Route: GET /
         * (SSR) Renders the homepage with filters.
         */
        get("/", (req, res) -> {
            String minPrice = req.queryParams("minPrice");
            String maxPrice = req.queryParams("maxPrice");
            
            // This line (List<Item>) is why the import is needed
            List<Item> items = itemService.getAllItems(minPrice, maxPrice); 
            
            Map<String, Object> model = new HashMap<>();
            model.put("items", items);
            model.put("minPrice", minPrice);
            model.put("maxPrice", maxPrice);
            return templateEngine.render(new ModelAndView(model, "index.mustache"));
        });

        // The specific /ranking route MUST come BEFORE the wildcard /:id route
        /**
         * Route: GET /ranking
         * Displays the new server-side rendered ranking page.
         */
        get("/ranking", (req, res) -> {
            List<RankedOffer> topOffers = offerService.getTopRankedOffers();
            Map<String, Object> model = new HashMap<>();
            model.put("offers", topOffers);
            return templateEngine.render(new ModelAndView(model, "ranking.mustache"));
        });
        
        /**
         * Route: GET /:id
         * SPRINT 2 REFACTOR (Option 3)
         * Redirects to the static HTML shell of our new JS application.
         */
        get("/:id", (req, res) -> {
            res.redirect("/item.html?id=" + req.params(":id"));
            return null;
        });

        /**
         * Route: POST /:id/offer
         * SPRINT 2 REFACTOR (Option 3)
         * Handles the form submission from the JS app.
         */
        post("/:id/offer", (req, res) -> {
            String id = req.params(":id");
            String bidderName = req.queryParams("bidderName");
            String bidderEmail = req.queryParams("bidderEmail");
            
            double offerAmount = 0.0;
            try {
                offerAmount = Double.parseDouble(req.queryParams("offerAmount"));
            } catch (NumberFormatException e) {
                res.status(400); // Bad Request
                return "{\"error\":\"Invalid offer amount\"}";
            }

            // Create and save the offer (this now throws an error if low)
            Offer newOffer = new Offer(bidderName, bidderEmail, id, offerAmount);
            offerService.addOffer(newOffer);
            
            System.out.println("[OFFER SAVED] Item ID: " + id + 
                               ", Bidder: " + bidderName + 
                               ", Amount: $" + offerAmount);

            // Broadcast the WebSocket update
            boolean updated = itemService.updateItemPrice(id, offerAmount);
            if (updated) {
                PriceUpdateWebSocketHandler.broadcastPriceUpdate(id, offerAmount);
            }

            res.status(201); // 201 Created
            return "{\"success\":true, \"newPrice\":" + offerAmount + "}";
        });
    }
}