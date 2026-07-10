package com.cardealership.service;

import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.OutOfStockException;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.VehicleRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final VehicleRepository vehicleRepository;

    public InventoryService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse purchaseVehicle(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        if (vehicle.getQuantity() < quantity) {
            throw new OutOfStockException("Insufficient stock. Available: " + vehicle.getQuantity());
        }

        vehicle.setQuantity(vehicle.getQuantity() - quantity);
        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    public VehicleResponse restockVehicle(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        vehicle.setQuantity(vehicle.getQuantity() + quantity);
        Vehicle saved = vehicleRepository.save(vehicle);
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