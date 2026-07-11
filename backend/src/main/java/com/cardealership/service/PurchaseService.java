package com.cardealership.service;

import com.cardealership.entity.Purchase;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.PurchaseRepository;
import com.cardealership.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final VehicleRepository vehicleRepository;

    public PurchaseService(PurchaseRepository purchaseRepository, VehicleRepository vehicleRepository) {
        this.purchaseRepository = purchaseRepository;
        this.vehicleRepository = vehicleRepository;
    }

    public Purchase recordPurchase(Long vehicleId, User user, int quantity) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + vehicleId));

        BigDecimal unitPrice = vehicle.getPrice();
        BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));

        Purchase purchase = Purchase.builder()
                .user(user)
                .vehicle(vehicle)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .purchasedAt(LocalDateTime.now())
                .build();

        return purchaseRepository.save(purchase);
    }
}
