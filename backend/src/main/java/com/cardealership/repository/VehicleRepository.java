package com.cardealership.repository;

import com.cardealership.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long>, JpaSpecificationExecutor<Vehicle> {

    Page<Vehicle> findByMakeContainingIgnoreCase(String make, Pageable pageable);

    Page<Vehicle> findByCategoryIgnoreCase(String category, Pageable pageable);

    default Page<Vehicle> searchVehicles(String make, String model, String category,
                                          BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return findAll(VehicleSpecifications.searchByFilters(make, model, category, minPrice, maxPrice), pageable);
    }
}