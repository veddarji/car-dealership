package com.cardealership.service;

import com.cardealership.entity.Purchase;
import com.cardealership.entity.User;
import com.cardealership.entity.Vehicle;
import com.cardealership.enums.Role;
import com.cardealership.repository.PurchaseRepository;
import com.cardealership.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseRepository purchaseRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private PurchaseService purchaseService;

    @Test
    void recordPurchase_ShouldCalculateTotalAndSave() {
        Vehicle vehicle = Vehicle.builder()
                .id(1L).make("Toyota").model("Camry")
                .price(new BigDecimal("25000")).quantity(10).build();
        User user = User.builder().id(1L).username("alice").role(Role.USER).build();

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(purchaseRepository.save(any())).then(inv -> inv.getArgument(0));

        Purchase result = purchaseService.recordPurchase(1L, user, 2);

        assertThat(result.getQuantity()).isEqualTo(2);
        assertThat(result.getUnitPrice()).isEqualByComparingTo("25000");
        assertThat(result.getTotalPrice()).isEqualByComparingTo("50000");
        assertThat(result.getUser().getId()).isEqualTo(1L);
        assertThat(result.getVehicle().getId()).isEqualTo(1L);
        verify(purchaseRepository).save(any());
    }
}
