package com.cardealership.exception;

/**
 * Thrown when a purchase cannot be fulfilled due to insufficient inventory.
 */
public class OutOfStockException extends RuntimeException {

    public OutOfStockException(String message) {
        super(message);
    }
}
