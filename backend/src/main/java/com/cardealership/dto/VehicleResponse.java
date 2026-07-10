package com.cardealership.dto;

import java.math.BigDecimal;

/**
 * DTO for returning vehicle data to the client.
 *
 * @param id       the vehicle ID
 * @param make     the vehicle manufacturer
 * @param model    the vehicle model name
 * @param category the vehicle category
 * @param price    the vehicle price
 * @param quantity the number of units in stock
 */
public record VehicleResponse(
        Long id,
        String make,
        String model,
        String category,
        BigDecimal price,
        Integer quantity
) {}
