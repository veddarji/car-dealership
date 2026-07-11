package com.cardealership.config;

import com.cardealership.entity.Category;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.enums.Role;
import com.cardealership.repository.CategoryRepository;
import com.cardealership.repository.UserRepository;
import com.cardealership.repository.VehicleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, VehicleRepository vehicleRepository,
                      CategoryRepository categoryRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@cardealership.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build());

        List.of("Sedan", "SUV", "Coupe", "Truck", "Convertible", "Hatchback", "Van")
                .forEach(name -> categoryRepository.save(new Category(null, name, null)));

        List<Vehicle> vehicles = List.of(
                Vehicle.builder().make("Toyota").model("Camry").category("Sedan").price(new BigDecimal("27400")).quantity(5).build(),
                Vehicle.builder().make("Toyota").model("RAV4").category("SUV").price(new BigDecimal("29800")).quantity(6).build(),
                Vehicle.builder().make("Honda").model("CR-V").category("SUV").price(new BigDecimal("33200")).quantity(3).build(),
                Vehicle.builder().make("Honda").model("Accord").category("Sedan").price(new BigDecimal("28500")).quantity(4).build(),
                Vehicle.builder().make("Ford").model("Mustang").category("Coupe").price(new BigDecimal("43200")).quantity(2).build(),
                Vehicle.builder().make("Ford").model("F-150").category("Truck").price(new BigDecimal("37000")).quantity(4).build(),
                Vehicle.builder().make("BMW").model("3 Series").category("Sedan").price(new BigDecimal("43500")).quantity(4).build(),
                Vehicle.builder().make("BMW").model("X5").category("SUV").price(new BigDecimal("61500")).quantity(2).build(),
                Vehicle.builder().make("Tesla").model("Model 3").category("Sedan").price(new BigDecimal("41900")).quantity(3).build(),
                Vehicle.builder().make("Tesla").model("Model Y").category("SUV").price(new BigDecimal("47900")).quantity(2).build(),
                Vehicle.builder().make("Chevrolet").model("Tahoe").category("SUV").price(new BigDecimal("52500")).quantity(3).build(),
                Vehicle.builder().make("Jeep").model("Wrangler").category("SUV").price(new BigDecimal("35000")).quantity(4).build()
        );
        vehicleRepository.saveAll(vehicles);
    }
}