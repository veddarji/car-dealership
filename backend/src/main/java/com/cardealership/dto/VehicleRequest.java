package com.cardealership.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO for creating or updating a vehicle.
 *
 * @param make     the vehicle manufacturer
 * @param model    the vehicle model name
 * @param category the vehicle category (e.g. SUV, Sedan, Truck)
 * @param price    the vehicle price
 * @param quantity the number of units in stock
 */
public record VehicleRequest(
        @NotBlank(message = "Make is required")
        String make,

        @NotBlank(message = "Model is required")
        String model,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Quantity must be zero or positive")
        Integer quantity
) {}
