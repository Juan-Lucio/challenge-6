package com.collectibles.offer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OfferService.
 * We will test the in-memory logic for adding and retrieving offers.
 */
class OfferServiceTest {

    private OfferService offerService;

    // This method runs before each @Test
    @BeforeEach
    void setUp() {
        // Arrange: Create a fresh service for every test
        offerService = new OfferService();
    }

    @Test
    @DisplayName("Should correctly add a new offer")
    void testAddOffer() {
        // Arrange: Create a new offer
        Offer offer = new Offer("Test User", "test@email.com", "item1", 100.0);

        // Act: Add the offer
        offerService.addOffer(offer);

        // Assert: Check that the offer was saved
        List<Offer> offers = offerService.getOffersByItemId("item1");
        assertNotNull(offers);
        assertEquals(1, offers.size());
        assertEquals("Test User", offers.get(0).getName());
    }

    @Test
    @DisplayName("Should return offers for the correct item ID")
    void testGetOffersByItemId() {
        // Arrange: Add multiple offers for different items
        offerService.addOffer(new Offer("User A", "a@email.com", "item1", 100.0));
        offerService.addOffer(new Offer("User B", "b@email.com", "item2", 200.0));
        offerService.addOffer(new Offer("User C", "c@email.com", "item1", 150.0));

        // Act & Assert
        // Check item1
        List<Offer> item1Offers = offerService.getOffersByItemId("item1");
        assertEquals(2, item1Offers.size());
        assertEquals(150.0, item1Offers.get(1).getAmount());

        // Check item2
        List<Offer> item2Offers = offerService.getOffersByItemId("item2");
        assertEquals(1, item2Offers.size());
        assertEquals("User B", item2Offers.get(0).getName());
    }

    @Test
    @DisplayName("Should return empty list for an item with no offers")
    void testGetOffersForNonExistentItem() {
        // Act: Request offers for an item that has none
        List<Offer> offers = offerService.getOffersByItemId("item-nonexistent");

        // Assert: Check that the list is empty and not null
        assertNotNull(offers);
        assertTrue(offers.isEmpty());
    }
}