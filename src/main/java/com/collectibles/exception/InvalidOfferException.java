package com.collectibles.exception;

/**
 * Custom exception thrown when a new offer is not
 * higher than the current maximum offer.
 */
public class InvalidOfferException extends RuntimeException {
    
    public InvalidOfferException(String message) {
        super(message);
    }
}
