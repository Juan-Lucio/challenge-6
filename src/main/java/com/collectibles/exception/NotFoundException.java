package com.collectibles.exception;

/**
 * Custom exception to be thrown when a resource (like an Item or User)
 * is not found. This allows us to handle 404 errors gracefully
 * and show a custom error page.
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
}