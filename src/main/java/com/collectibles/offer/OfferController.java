package com.collectibles.offer;

import com.collectibles.utils.JsonUtil;
import java.util.List;
import static spark.Spark.*;

/**
 * Controller for handling all API routes related to Offers.
 * This is part of the S2 refactor to support the client-side app.
 */
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    /**
     * Registers all API routes for the Offer controller.
     * Called by App.java inside the /api group.
     */
    public void registerRoutes() {
        
        // Defines the route group for /api/offers
        path("/offers", () -> {

            /**
             * GET /api/offers/:itemId
             * Retrieves all offers for a specific item.
             */
            get("/:itemId", (req, res) -> {
                String itemId = req.params(":itemId");
                List<Offer> offers = offerService.getOffersByItemId(itemId);
                return offers;
            }, JsonUtil::toJson); // Convert list to JSON
            
            // We could add POST/PUT/DELETE here later if needed
        });
    }
}