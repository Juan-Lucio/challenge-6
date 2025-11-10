package com.collectibles;

import com.collectibles.item.Item; 
import com.collectibles.item.ItemService;
import com.collectibles.offer.Offer;
import com.collectibles.offer.OfferService;
import com.collectibles.websocket.PriceUpdateWebSocketHandler;
import spark.ModelAndView;
import spark.TemplateEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;

/**
 * WebController
 * SPRINT 2 REFACTOR: This controller now only handles the Server-Side Rendered (SSR)
 * homepage. The item detail page is refactored to a static file + JS app.
 * The POST route is updated to support this new JS app.
 */
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
         * (SSR) Renders the homepage with filters. This remains unchanged.
         */
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

        /**
         * Route: GET /:id
         * SPRINT 2 REFACTOR (Opción 3)
         * This route no longer renders a template.
         * It redirects to the static HTML shell of our new JS application.
         * This decouples the frontend from the backend.
         */
        get("/:id", (req, res) -> {
            // We pass the id in the URL so the JS app can read it
            res.redirect("/item.html?id=" + req.params(":id"));
            return null;
        });

        /**
         * Route: POST /:id/offer
         * SPRINT 2 REFACTOR (Opción 3)
         * This route is now called via fetch() from the JS app.
         * It must return JSON instead of redirecting.
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

            // Create and save the offer
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

            // Return a success JSON message
            res.status(201); // 201 Created
            return "{\"success\":true, \"newPrice\":" + offerAmount + "}";
        });
    }
}