package com.cardealership.repository;

import com.cardealership.entity.Category;
import com.cardealership.entity.InventoryTransaction;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryTransactionRepositoryTest {

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User admin;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        admin = userRepository.save(User.builder()
                .username("admin").email("admin@test.com")
                .password("password").role(Role.ADMIN).build());

        Category cat = categoryRepository.save(new Category(null, "SUV", null));

        vehicle = vehicleRepository.save(Vehicle.builder()
                .make("Honda").model("CR-V").category(cat.getName())
                .price(new BigDecimal("33200")).quantity(5).build());
    }

    @Test
    void saveTransaction_ShouldPersist() {
        InventoryTransaction tx = InventoryTransaction.builder()
                .vehicle(vehicle).user(admin)
                .type("PURCHASE").quantityChange(-2)
                .previousQuantity(5).newQuantity(3)
                .build();

        InventoryTransaction saved = transactionRepository.save(tx);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo("PURCHASE");
        assertThat(saved.getNewQuantity()).isEqualTo(3);
    }

    @Test
    void findByVehicleId_ShouldReturnHistory() {
        transactionRepository.save(InventoryTransaction.builder()
                .vehicle(vehicle).user(admin)
                .type("RESTOCK").quantityChange(5)
                .previousQuantity(0).newQuantity(5)
                .build());

        List<InventoryTransaction> history =
                transactionRepository.findByVehicleIdOrderByCreatedAtDesc(vehicle.getId());

        assertThat(history).hasSize(1);
    }
}
