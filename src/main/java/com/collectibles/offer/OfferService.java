package com.collectibles.offer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service layer for managing Offers.
 * This class stores offers in memory in a thread-safe way.
 */
public class OfferService {

    /**
     * In-memory database for offers.
     * We map an ItemID (String) to a List of all offers for that item.
     * We use ConcurrentHashMap and CopyOnWriteArrayList for thread-safety,
     * which is critical in a web server environment.
     */
    private final Map<String, List<Offer>> offersByItemId = new ConcurrentHashMap<>();

    /**
     * Adds a new offer to the in-memory store.
     * @param offer The Offer object to add.
     */
    public void addOffer(Offer offer) {
        // computeIfAbsent ensures the List is created if it's the first offer for this item
        List<Offer> offers = offersByItemId.computeIfAbsent(
            offer.getItemId(), 
            k -> new CopyOnWriteArrayList<>()
        );
        offers.add(offer);
    }

    /**
     * Retrieves all offers for a specific item.
     * @param itemId The ID of the item to get offers for.
     * @return A List of offers, or an empty list if none exist.
     */
    public List<Offer> getOffersByItemId(String itemId) {
        // getOrDefault safely returns an empty list instead of null
        return offersByItemId.getOrDefault(itemId, Collections.emptyList());
    }
}