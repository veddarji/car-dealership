package com.cardealership.controller;

import com.cardealership.dto.PagedResponse;
import com.cardealership.dto.PurchaseRequest;
import com.cardealership.dto.VehicleRequest;
import com.cardealership.dto.VehicleResponse;
import com.cardealership.service.InventoryService;
import com.cardealership.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Vehicles", description = "Vehicle inventory management endpoints")
public class VehicleController {

    private final VehicleService vehicleService;
    private final InventoryService inventoryService;

    public VehicleController(VehicleService vehicleService, InventoryService inventoryService) {
        this.vehicleService = vehicleService;
        this.inventoryService = inventoryService;
    }

    @Operation(summary = "Create a new vehicle", description = "Adds a new vehicle to the inventory (Admin only)")
    @ApiResponse(responseCode = "201", description = "Vehicle created successfully",
        content = @Content(schema = @Schema(implementation = VehicleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @PostMapping
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all vehicles", description = "Retrieves a paginated list of all vehicles")
    @ApiResponse(responseCode = "200", description = "Paginated list of vehicles")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping
    public ResponseEntity<PagedResponse<VehicleResponse>> getAllVehicles(@ParameterObject Pageable pageable) {
        Page<VehicleResponse> result = vehicleService.getAllVehicles(pageable);
        return ResponseEntity.ok(toPagedResponse(result));
    }

    @Operation(summary = "Search vehicles with filters", description = "Searches vehicles by make, model, category, and price range with pagination")
    @ApiResponse(responseCode = "200", description = "Paginated list of matching vehicles")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/search")
    public ResponseEntity<PagedResponse<VehicleResponse>> searchVehicles(
            @Parameter(description = "Vehicle make (partial match, case-insensitive)")
            @RequestParam(required = false) String make,
            @Parameter(description = "Vehicle model (partial match, case-insensitive)")
            @RequestParam(required = false) String model,
            @Parameter(description = "Vehicle category (exact match, case-insensitive)")
            @RequestParam(required = false) String category,
            @Parameter(description = "Minimum price filter")
            @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum price filter")
            @RequestParam(required = false) BigDecimal maxPrice,
            @ParameterObject Pageable pageable) {
        Page<VehicleResponse> result = vehicleService.searchVehicles(make, model, category, minPrice, maxPrice, pageable);
        return ResponseEntity.ok(toPagedResponse(result));
    }

    @Operation(summary = "Get vehicle by ID", description = "Retrieves a single vehicle by its ID")
    @ApiResponse(responseCode = "200", description = "Vehicle found",
        content = @Content(schema = @Schema(implementation = VehicleResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a vehicle", description = "Updates an existing vehicle (Admin only)")
    @ApiResponse(responseCode = "200", description = "Vehicle updated successfully",
        content = @Content(schema = @Schema(implementation = VehicleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id, @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a vehicle", description = "Removes a vehicle from the inventory (Admin only)")
    @ApiResponse(responseCode = "204", description = "Vehicle deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Purchase a vehicle", description = "Decreases vehicle stock by 1 (Authenticated users)")
    @ApiResponse(responseCode = "200", description = "Vehicle purchased successfully",
        content = @Content(schema = @Schema(implementation = VehicleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Insufficient stock")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @PostMapping("/{id}/purchase")
    public ResponseEntity<VehicleResponse> purchaseVehicle(
            @PathVariable Long id, @RequestBody @Valid PurchaseRequest request) {
        VehicleResponse response = inventoryService.purchaseVehicle(id, request.quantity());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Restock a vehicle", description = "Increases vehicle stock quantity (Admin only)")
    @ApiResponse(responseCode = "200", description = "Vehicle restocked successfully",
        content = @Content(schema = @Schema(implementation = VehicleResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid quantity")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
    @ApiResponse(responseCode = "404", description = "Vehicle not found")
    @PostMapping("/{id}/restock")
    public ResponseEntity<VehicleResponse> restockVehicle(
            @PathVariable Long id, @RequestBody @Valid PurchaseRequest request) {
        VehicleResponse response = inventoryService.restockVehicle(id, request.quantity());
        return ResponseEntity.ok(response);
    }

    private <T> PagedResponse<T> toPagedResponse(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages(), page.isLast());
    }
}