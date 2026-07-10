package com.cardealership.service;

import com.cardealership.dto.VehicleRequest;
import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.Vehicle;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository);
    }

    @Test
    void createVehicle_WithValidData_ShouldReturnVehicleResponse() {
        VehicleRequest request = new VehicleRequest("Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse response = vehicleService.createVehicle(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.make()).isEqualTo("Toyota");
        assertThat(response.model()).isEqualTo("Camry");
        assertThat(response.category()).isEqualTo("Sedan");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("25000"));
        assertThat(response.quantity()).isEqualTo(10);
    }

    @Test
    void createVehicle_WithBlankMake_ShouldThrowException() {
        VehicleRequest request = new VehicleRequest("", "Camry", "Sedan",
                new BigDecimal("25000"), 10);

        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createVehicle_WithNegativePrice_ShouldThrowException() {
        VehicleRequest request = new VehicleRequest("Toyota", "Camry", "Sedan",
                new BigDecimal("-100"), 10);

        assertThatThrownBy(() -> vehicleService.createVehicle(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getAllVehicles_ShouldReturnPagedList() {
        Pageable pageable = PageRequest.of(0, 10);
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));

        when(vehicleRepository.findAll(pageable)).thenReturn(page);

        Page<VehicleResponse> result = vehicleService.getAllVehicles(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).make()).isEqualTo("Toyota");
    }

    @Test
    void getVehicleById_WithValidId_ShouldReturnVehicle() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        VehicleResponse response = vehicleService.getVehicleById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.make()).isEqualTo("Toyota");
    }

    @Test
    void getVehicleById_WithInvalidId_ShouldThrowException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.getVehicleById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void searchVehicles_WithMakeFilter_ShouldReturnFilteredList() {
        Pageable pageable = PageRequest.of(0, 10);
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));

        when(vehicleRepository.searchVehicles(eq("Toyota"), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(page);

        Page<VehicleResponse> result = vehicleService.searchVehicles("Toyota", null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).make()).isEqualTo("Toyota");
    }

    @Test
    void searchVehicles_WithPriceRange_ShouldReturnFilteredList() {
        Pageable pageable = PageRequest.of(0, 10);
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));

        when(vehicleRepository.searchVehicles(any(), any(), any(), eq(new BigDecimal("10000")),
                eq(new BigDecimal("30000")), eq(pageable))).thenReturn(page);

        Page<VehicleResponse> result = vehicleService.searchVehicles(null, null, null,
                new BigDecimal("10000"), new BigDecimal("30000"), pageable);

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void searchVehicles_WithPagination_ShouldReturnCorrectPage() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Vehicle> emptyPage = Page.empty();

        when(vehicleRepository.searchVehicles(any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(emptyPage);

        Page<VehicleResponse> result = vehicleService.searchVehicles(null, null, null, null, null, pageable);

        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void updateVehicle_WithValidData_ShouldReturnUpdatedVehicle() {
        VehicleRequest request = new VehicleRequest("Honda", "Accord", "Sedan",
                new BigDecimal("28000"), 8);
        Vehicle existing = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();
        Vehicle updated = Vehicle.builder()
                .id(1L).make("Honda").model("Accord").category("Sedan")
                .price(new BigDecimal("28000")).quantity(8).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updated);

        VehicleResponse response = vehicleService.updateVehicle(1L, request);

        assertThat(response.make()).isEqualTo("Honda");
        assertThat(response.model()).isEqualTo("Accord");
        assertThat(response.price()).isEqualByComparingTo(new BigDecimal("28000"));
    }

    @Test
    void updateVehicle_WithInvalidId_ShouldThrowException() {
        VehicleRequest request = new VehicleRequest("Honda", "Accord", "Sedan",
                new BigDecimal("28000"), 8);

        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.updateVehicle(99L, request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteVehicle_WithValidId_ShouldDelete() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("25000")).quantity(10).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        doNothing().when(vehicleRepository).delete(vehicle);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository).delete(vehicle);
    }

    @Test
    void deleteVehicle_WithInvalidId_ShouldThrowException() {
        when(vehicleRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehicleService.deleteVehicle(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}