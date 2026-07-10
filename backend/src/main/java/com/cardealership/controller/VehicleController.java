package com.cardealership.controller;

import com.cardealership.dto.PagedResponse;
import com.cardealership.dto.VehicleRequest;
import com.cardealership.dto.VehicleResponse;
import com.cardealership.service.InventoryService;
import com.cardealership.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;
    private final InventoryService inventoryService;

    public VehicleController(VehicleService vehicleService, InventoryService inventoryService) {
        this.vehicleService = vehicleService;
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PagedResponse<VehicleResponse>> getAllVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<VehicleResponse> result = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(toPagedResponse(result));
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<VehicleResponse>> searchVehicles(
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        Page<VehicleResponse> result = vehicleService.searchVehicles(make, model, category, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(toPagedResponse(result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<VehicleResponse> purchaseVehicle(@PathVariable Long id) {
        VehicleResponse response = inventoryService.purchaseVehicle(id, 1);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/restock")
    public ResponseEntity<VehicleResponse> restockVehicle(
            @PathVariable Long id, @RequestBody @Valid com.cardealership.dto.PurchaseRequest request) {
        VehicleResponse response = inventoryService.restockVehicle(id, request.quantity());
        return ResponseEntity.ok(response);
    }

    private Pageable buildPageable(int page, int size, String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, field));
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}