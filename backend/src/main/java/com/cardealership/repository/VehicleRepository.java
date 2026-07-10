package com.cardealership.repository;

import com.cardealership.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Spring Data JPA repository for {@link Vehicle} entities.
 * Provides filtering and multi-criteria search capabilities.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Finds vehicles whose make contains the given string (case-insensitive).
     */
    List<Vehicle> findByMakeContainingIgnoreCase(String make);

    /**
     * Finds vehicles whose model contains the given string (case-insensitive).
     */
    List<Vehicle> findByModelContainingIgnoreCase(String model);

    /**
     * Finds vehicles matching the exact category (case-insensitive).
     */
    List<Vehicle> findByCategoryIgnoreCase(String category);

    /**
     * Searches vehicles using multiple optional filters.
     * Any parameter can be {@code null} to skip that filter.
     *
     * @param make     partial make name (case-insensitive)
     * @param model    partial model name (case-insensitive)
     * @param category exact category (case-insensitive)
     * @param minPrice minimum price (inclusive)
     * @param maxPrice maximum price (inclusive)
     * @return list of matching vehicles
     */
    @Query("SELECT v FROM Vehicle v WHERE " +
           "(:make IS NULL OR LOWER(v.make) LIKE LOWER(CONCAT('%', :make, '%'))) AND " +
           "(:model IS NULL OR LOWER(v.model) LIKE LOWER(CONCAT('%', :model, '%'))) AND " +
           "(:category IS NULL OR LOWER(v.category) = LOWER(:category)) AND " +
           "(:minPrice IS NULL OR v.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR v.price <= :maxPrice)")
    List<Vehicle> searchVehicles(@Param("make") String make,
                                 @Param("model") String model,
                                 @Param("category") String category,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice);
}
