package com.cardealership.service;

import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.OutOfStockException;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryService(vehicleRepository);
    }

    @Test
    void purchaseVehicle_WithSufficientStock_ShouldDecreaseQuantity() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponse response = inventoryService.purchaseVehicle(1L, 3);

        assertThat(response.quantity()).isEqualTo(7);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void purchaseVehicle_WithInsufficientStock_ShouldThrowException() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(2).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThatThrownBy(() -> inventoryService.purchaseVehicle(1L, 5))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void purchaseVehicle_WithZeroQuantity_ShouldThrowException() {
        assertThatThrownBy(() -> inventoryService.purchaseVehicle(1L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void purchaseVehicle_WithNonExistentId_ShouldThrowException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.purchaseVehicle(99L, 1))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void restockVehicle_WithValidData_ShouldIncreaseQuantity() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenAnswer(i -> i.getArgument(0));

        VehicleResponse response = inventoryService.restockVehicle(1L, 5);

        assertThat(response.quantity()).isEqualTo(15);
        verify(vehicleRepository).save(vehicle);
    }

    @Test
    void restockVehicle_WithNonExistentId_ShouldThrowException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.restockVehicle(99L, 5))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void restockVehicle_WithZeroQuantity_ShouldThrowException() {
        assertThatThrownBy(() -> inventoryService.restockVehicle(1L, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }
}