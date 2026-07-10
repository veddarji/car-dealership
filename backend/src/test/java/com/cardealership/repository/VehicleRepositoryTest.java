package com.cardealership.repository;

import com.cardealership.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();

        vehicleRepository.save(Vehicle.builder()
                .make("Toyota").model("Camry").category("Sedan")
                .price(new BigDecimal("27400")).quantity(5).build());
        vehicleRepository.save(Vehicle.builder()
                .make("Honda").model("CR-V").category("SUV")
                .price(new BigDecimal("33200")).quantity(3).build());
        vehicleRepository.save(Vehicle.builder()
                .make("Ford").model("Mustang").category("Coupe")
                .price(new BigDecimal("43200")).quantity(2).build());
        vehicleRepository.save(Vehicle.builder()
                .make("BMW").model("3 Series").category("Sedan")
                .price(new BigDecimal("43500")).quantity(4).build());
        vehicleRepository.save(Vehicle.builder()
                .make("Toyota").model("RAV4").category("SUV")
                .price(new BigDecimal("29800")).quantity(6).build());
    }

    @Test
    void saveVehicle_ShouldPersistWithGeneratedId() {
        Vehicle vehicle = Vehicle.builder()
                .make("Tesla").model("Model 3").category("Sedan")
                .price(new BigDecimal("41900")).quantity(3).build();

        Vehicle saved = vehicleRepository.save(vehicle);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getMake()).isEqualTo("Tesla");
        assertThat(saved.getQuantity()).isEqualTo(3);
    }

    @Test
    void findAll_ShouldReturnPagedResults() {
        PageRequest pageable = PageRequest.of(0, 2, Sort.by("price").ascending());

        Page<Vehicle> page = vehicleRepository.findAll(pageable);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getContent().get(0).getPrice()).isEqualByComparingTo(new BigDecimal("27400"));
    }

    @Test
    void searchVehicles_WithAllFilters_ShouldReturnFiltered() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Vehicle> result = vehicleRepository.searchVehicles(
                "Toyota", null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getMake()).isEqualTo("Toyota");
    }

    @Test
    void searchVehicles_WithNoFilters_ShouldReturnAll() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Vehicle> result = vehicleRepository.searchVehicles(
                null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(5);
    }

    @Test
    void searchVehicles_WithPriceRange_ShouldReturnFiltered() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Vehicle> result = vehicleRepository.searchVehicles(
                null, null, null,
                new BigDecimal("30000"), new BigDecimal("45000"), pageable);

        assertThat(result.getContent()).hasSize(3);
    }

    @Test
    void searchVehicles_WithCategory_ShouldReturnFiltered() {
        PageRequest pageable = PageRequest.of(0, 10);

        Page<Vehicle> result = vehicleRepository.searchVehicles(
                null, null, "SUV", null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void searchVehicles_WithPagination_ShouldReturnCorrectPage() {
        PageRequest pageable = PageRequest.of(0, 2);

        Page<Vehicle> firstPage = vehicleRepository.searchVehicles(
                null, null, null, null, null, pageable);

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalPages()).isEqualTo(3);
    }
}
