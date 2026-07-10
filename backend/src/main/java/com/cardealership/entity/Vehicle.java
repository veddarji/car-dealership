package com.cardealership.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * JPA entity representing a vehicle in the dealership inventory.
 */
@Entity
@Table(name = "vehicles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Make is required")
    private String make;

    @Column(nullable = false)
    @NotBlank(message = "Model is required")
    private String model;

    @Column(nullable = false)
    @NotBlank(message = "Category is required")
    private String category;

    @Column(nullable = false)
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @Column(nullable = false)
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;
}
