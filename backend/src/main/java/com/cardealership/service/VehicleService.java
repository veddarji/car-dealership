package com.cardealership.service;

import com.cardealership.dto.VehicleRequest;
import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.InventoryTransactionRepository;
import com.cardealership.repository.PurchaseRepository;
import com.cardealership.repository.VehicleRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final PurchaseRepository purchaseRepository;
    private final InventoryTransactionRepository transactionRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          PurchaseRepository purchaseRepository,
                          InventoryTransactionRepository transactionRepository) {
        this.vehicleRepository = vehicleRepository;
        this.purchaseRepository = purchaseRepository;
        this.transactionRepository = transactionRepository;
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse createVehicle(VehicleRequest request) {
        if (request.make() == null || request.make().isBlank()) {
            throw new IllegalArgumentException("Make is required");
        }
        if (request.price() != null && request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }

        Vehicle vehicle = Vehicle.builder()
                .make(request.make())
                .model(request.model())
                .category(request.category())
                .price(request.price())
                .quantity(request.quantity())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        return toResponse(saved);
    }

    @Cacheable(value = "vehicles", key = "#pageable")
    public Page<VehicleResponse> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable).map(this::toResponse);
    }

    @Cacheable(value = "vehicles", key = "'vehicle-' + #id")
    public VehicleResponse getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));
    }

    public Page<VehicleResponse> searchVehicles(String make, String model, String category,
                                                 BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return vehicleRepository.searchVehicles(make, model, category, minPrice, maxPrice, pageable)
                .map(this::toResponse);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle existing = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with id: " + id));

        existing.setMake(request.make());
        existing.setModel(request.model());
        existing.setCategory(request.category());
        existing.setPrice(request.price());
        existing.setQuantity(request.quantity());

        Vehicle saved = vehicleRepository.save(existing);
        return toResponse(saved);
    }

    @CacheEvict(value = "vehicles", allEntries = true)
    @Transactional
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehicle not found with id: " + id);
        }
        transactionRepository.findByVehicleIdOrderByCreatedAtDesc(id)
                .forEach(tx -> transactionRepository.delete(tx));
        purchaseRepository.findByVehicleIdOrderByPurchasedAtDesc(id)
                .forEach(p -> purchaseRepository.delete(p));
        vehicleRepository.deleteById(id);
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