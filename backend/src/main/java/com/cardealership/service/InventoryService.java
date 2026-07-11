package com.cardealership.service;

import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.InventoryTransaction;
import com.cardealership.entity.Purchase;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.OutOfStockException;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.InventoryTransactionRepository;
import com.cardealership.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final VehicleRepository vehicleRepository;
    private final PurchaseService purchaseService;
    private final InventoryTransactionRepository transactionRepository;

    public InventoryService(VehicleRepository vehicleRepository,
                            PurchaseService purchaseService,
                            InventoryTransactionRepository transactionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.purchaseService = purchaseService;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public VehicleResponse purchaseVehicle(Long id, int quantity, User user) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (vehicle.getQuantity() < quantity) {
            throw new OutOfStockException("Insufficient stock. Available: " + vehicle.getQuantity());
        }

        int previousQty = vehicle.getQuantity();
        vehicle.setQuantity(previousQty - quantity);
        Vehicle saved = vehicleRepository.save(vehicle);

        purchaseService.recordPurchase(id, user, quantity);

        transactionRepository.save(InventoryTransaction.builder()
                .vehicle(saved).user(user)
                .type("PURCHASE").quantityChange(-quantity)
                .previousQuantity(previousQty).newQuantity(saved.getQuantity())
                .build());

        return toResponse(saved);
    }

    @Transactional
    public VehicleResponse restockVehicle(Long id, int quantity, User user) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        int previousQty = vehicle.getQuantity();
        vehicle.setQuantity(previousQty + quantity);
        Vehicle saved = vehicleRepository.save(vehicle);

        transactionRepository.save(InventoryTransaction.builder()
                .vehicle(saved).user(user)
                .type("RESTOCK").quantityChange(quantity)
                .previousQuantity(previousQty).newQuantity(saved.getQuantity())
                .build());

        return toResponse(saved);
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getCategory(),
                vehicle.getPrice(),
                vehicle.getQuantity()
        );
    }
}