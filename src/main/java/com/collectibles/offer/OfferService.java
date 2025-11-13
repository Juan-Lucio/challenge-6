package com.collectibles.offer;

import com.collectibles.exception.InvalidOfferException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementException;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing Offers.
 * Refactored to use Jdbi and to enforce business logic.
 */
public class OfferService {

    private final Jdbi jdbi;

    public OfferService(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    /**
     * Retrieves all offers for a specific item, ordered highest first.
     * @param itemId The ID of the item.
     * @return A List of offers.
     */
    public List<Offer> getOffersByItemId(String itemId) {
        return jdbi.withHandle(handle -> 
            handle.createQuery(
                "SELECT * FROM offers WHERE item_id = :itemId ORDER BY amount DESC")
                .bind("itemId", itemId)
                .mapToBean(Offer.class)
                .list()
        );
    }

    /**
     * Adds a new offer, but only if it's higher than the current max offer.
     * This entire method runs in a single database transaction.
     * @param newOffer The offer to add.
     * @throws InvalidOfferException if the offer is not high enough.
     */
    public void addOffer(Offer newOffer) throws InvalidOfferException {
        try {
            jdbi.useTransaction(handle -> {
                // 1. Get the current highest offer for this item
                Optional<Double> maxOfferOpt = handle.createQuery(
                    "SELECT MAX(amount) FROM offers WHERE item_id = :itemId")
                    .bind("itemId", newOffer.getItem_id())
                    .mapTo(Double.class)
                    .findFirst();

                // 2. Enforce Business Logic (Req 2)
                if (maxOfferOpt.isPresent() && newOffer.getAmount() <= maxOfferOpt.get()) {
                    throw new InvalidOfferException(
                        "Offer must be higher than the current max bid of $" + maxOfferOpt.get());
                }

                // 3. If logic passes, insert the new offer
                handle.createUpdate(
                    "INSERT INTO offers (item_id, name, email, amount) " +
                    "VALUES (:item_id, :name, :email, :amount)")
                    .bindBean(newOffer)
                    .execute();
            });
        } catch (StatementException e) {
            // Unwrap the custom exception if it was thrown inside the transaction
            if (e.getCause() instanceof InvalidOfferException) {
                throw (InvalidOfferException) e.getCause();
            }
            throw e;
        }
    }

    /**
     * NEW FUNCTIONALITY (Ranking Page)
     * Retrieves the top 10 highest offers from across all items,
     * joining with the items table to get the item name.
     */
    public List<RankedOffer> getTopRankedOffers() {
        // Use "itemName" alias to respect Jdbi mapping
        String sql = "SELECT " +
                     "  o.amount, " +
                     "  o.name AS \"name\", " +  // Bidder name
                     "  o.email, " +
                     "  i.name AS \"itemName\" " + // Item name
                     "FROM offers o " +
                     "JOIN items i ON o.item_id = i.id " +
                     "ORDER BY o.amount DESC " +
                     "LIMIT 10";
                     
        return jdbi.withHandle(handle ->
            handle.createQuery(sql)
                  .mapToBean(RankedOffer.class)
                  .list()
        );
    }
}