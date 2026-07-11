package com.cardealership.repository;

import com.cardealership.entity.Category;
import com.cardealership.entity.Purchase;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PurchaseRepositoryTest {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User user;
    private Vehicle vehicle;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .username("buyer").email("buyer@test.com")
                .password("password").role(Role.USER).build());

        Category cat = categoryRepository.save(new Category(null, "Sedan", null));

        vehicle = vehicleRepository.save(Vehicle.builder()
                .make("Toyota").model("Camry").category(cat.getName())
                .price(new BigDecimal("25000")).quantity(10).build());
    }

    @Test
    void savePurchase_ShouldPersistWithGeneratedId() {
        Purchase purchase = Purchase.builder()
                .user(user).vehicle(vehicle)
                .quantity(2).unitPrice(new BigDecimal("25000"))
                .totalPrice(new BigDecimal("50000"))
                .purchasedAt(LocalDateTime.now())
                .build();

        Purchase saved = purchaseRepository.save(purchase);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getQuantity()).isEqualTo(2);
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getVehicle().getId()).isEqualTo(vehicle.getId());
    }

    @Test
    void findByUserId_ShouldReturnUserPurchases() {
        purchaseRepository.save(Purchase.builder()
                .user(user).vehicle(vehicle)
                .quantity(1).unitPrice(new BigDecimal("25000"))
                .totalPrice(new BigDecimal("25000"))
                .purchasedAt(LocalDateTime.now())
                .build());

        List<Purchase> purchases = purchaseRepository.findByUserIdOrderByPurchasedAtDesc(user.getId());

        assertThat(purchases).hasSize(1);
        assertThat(purchases.get(0).getVehicle().getMake()).isEqualTo("Toyota");
    }

    @Test
    void findByVehicleId_ShouldReturnVehiclePurchases() {
        purchaseRepository.save(Purchase.builder()
                .user(user).vehicle(vehicle)
                .quantity(3).unitPrice(new BigDecimal("25000"))
                .totalPrice(new BigDecimal("75000"))
                .purchasedAt(LocalDateTime.now())
                .build());

        List<Purchase> purchases = purchaseRepository.findByVehicleIdOrderByPurchasedAtDesc(vehicle.getId());

        assertThat(purchases).hasSize(1);
    }
}
