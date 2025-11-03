package com.collectibles;

import com.collectibles.exception.NotFoundException;
import com.collectibles.item.Item;
import com.collectibles.item.ItemService;
// --- (NUEVOS IMPORTS) ---
import com.collectibles.offer.Offer;
import com.collectibles.offer.OfferService;
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
    // --- (NUEVO SERVICIO) ---
    private final OfferService offerService;
    private final TemplateEngine templateEngine;

    // --- (CONSTRUCTOR ACTUALIZADO) ---
    public WebController(ItemService itemService, OfferService offerService, TemplateEngine templateEngine) {
        this.itemService = itemService;
        this.offerService = offerService;
        this.templateEngine = templateEngine;
    }

    public void registerRoutes() {

        // --- (GET /:id ACTUALIZADO) ---
        get("/:id", (req, res) -> {
            String id = req.params(":id");
            Optional<Item> itemOpt = itemService.getItemById(id);

            if (itemOpt.isEmpty()) {
                throw new NotFoundException("Item with ID '" + id + "' not found.");
            }

            // --- (NUEVA LÓGICA) ---
            // Get the list of offers for this item
            List<Offer> offers = offerService.getOffersByItemId(id);
            
            Map<String, Object> model = new HashMap<>();
            model.put("item", itemOpt.get());
            // Pass the list of offers to the template
            model.put("offers", offers);

            return templateEngine.render(new ModelAndView(model, "item.mustache"));
        });

        // --- (POST /:id/offer ACTUALIZADO) ---
        post("/:id/offer", (req, res) -> {
            String id = req.params(":id");
            
            // Get data from the form body
            String bidderName = req.queryParams("bidderName");
            // --- (NUEVO CAMPO) ---
            String bidderEmail = req.queryParams("bidderEmail");
            
            // --- (LÓGICA ACTUALIZADA) ---
            // Parse amount to double for correct data type
            double offerAmount = 0.0;
            try {
                offerAmount = Double.parseDouble(req.queryParams("offerAmount"));
            } catch (NumberFormatException e) {
                // Handle bad input later (e.g., show error message)
                // For now, redirect back
                res.redirect("/" + id);
                return null;
            }

            // Create new Offer object
            Offer newOffer = new Offer(bidderName, bidderEmail, id, offerAmount);
            
            // Save the offer using the service
            offerService.addOffer(newOffer);
            
            // Log to console (optional, but good for debugging)
            System.out.println("[OFFER SAVED] Item ID: " + id + 
                               ", Bidder: " + bidderName + 
                               ", Amount: $" + offerAmount);

            res.redirect("/" + id);
            return null;
        });

        // --- (GET / sin cambios) ---
        get("/", (req, res) -> {
            List<Item> items = itemService.getAllItems();
            Map<String, Object> model = new HashMap<>();
            model.put("items", items);
            return templateEngine.render(new ModelAndView(model, "index.mustache"));
        });
    }
}