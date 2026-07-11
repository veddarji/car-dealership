package com.cardealership;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for the Car Dealership Inventory System API.
 * Bootstraps the Spring Boot application context.
 */
@SpringBootApplication
@EnableCaching
public class CarDealershipApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarDealershipApplication.class, args);
    }
}
