package com.collectibles;

import com.collectibles.exception.NotFoundException;
import com.collectibles.item.Item;
import com.collectibles.item.ItemService;
import spark.ModelAndView;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static spark.Spark.*;

/**
 * WebController
 * Handles all routes related to the user-facing website (HTML rendering).
 * Routes registered here are NOT part of the /api group.
 */
public class WebController {

    private final ItemService itemService;
    private final TemplateEngine templateEngine;

    public WebController(ItemService itemService, TemplateEngine templateEngine) {
        this.itemService = itemService;
        this.templateEngine = templateEngine;
    }

    /**
     * Registers all web-facing routes.
     * Called by App.java.
     */
    public void registerRoutes() {

        // --- (Requirement 2: Views and Templates) ---

        /**
         * Route: GET /
         * Displays the homepage (index.mustache) with a list of all items.
         */
        get("/", (req, res) -> {
            // Use the S2 method that returns full objects
            List<Item> items = itemService.getAllItems();
            
            // We use a Map to pass data to the template.
            // The key "items" must match the {{#items}} tag in index.mustache
            Map<String, Object> model = new HashMap<>();
            model.put("items", items);

            // Render the index.mustache template with the model data
            return templateEngine.render(new ModelAndView(model, "index.mustache"));
        });

        /**
         * Route: GET /:id
         * Displays the detail page (item.mustache) for a single item.
         */
        get("/:id", (req, res) -> {
            String id = req.params(":id");
            // Use the S2 method that returns a full object
            Optional<Item> itemOpt = itemService.getItemById(id);

            if (itemOpt.isEmpty()) {
                // SPRINT 2 Exception Handling: Throw custom exception
                throw new NotFoundException("Item with ID '" + id + "' not found.");
            }

            Map<String, Object> model = new HashMap<>();
            model.put("item", itemOpt.get());

            // Render the item.mustache template
            return templateEngine.render(new ModelAndView(model, "item.mustache"));
        });


        // --- (Requirement 3: Web Form) ---

        /**
         * Route: POST /:id/offer
         * Handles the submission from the "Make an Offer" form.
         * This uses the Post-Redirect-Get (PRG) pattern for safety.
         */
        post("/:id/offer", (req, res) -> {
            String id = req.params(":id");
            
            // Get data from the form body
            String offerAmount = req.queryParams("offerAmount");
            String bidderName = req.queryParams("bidderName");

            // In a real app, you would validate and save this to a database.
            // For now, we just log it to the console to prove it works.
            System.out.println("[OFFER RECEIVED] Item ID: " + id + 
                               ", Bidder: " + bidderName + 
                               ", Amount: $" + offerAmount);

            // Redirect back to the item page to prevent form re-submission
            res.redirect("/" + id);
            return null; // The redirect handles the response
        });
    }
}