package com.cardealership.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for purchase and restock requests.
 * Quantity defaults to 1 when not provided (handled at the controller level).
 *
 * @param quantity the number of units to purchase or restock
 */
public record PurchaseRequest(
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        Integer quantity
) {
    /**
     * Compact constructor that defaults quantity to 1 when {@code null}.
     */
    public PurchaseRequest {
        if (quantity == null) {
            quantity = 1;
        }
    }
}
