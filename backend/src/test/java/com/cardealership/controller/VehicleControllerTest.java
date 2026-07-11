package com.cardealership.controller;

import com.cardealership.dto.VehicleResponse;
import com.cardealership.entity.User;
import com.cardealership.enums.Role;
import com.cardealership.exception.OutOfStockException;
import com.cardealership.exception.ResourceNotFoundException;
import com.cardealership.repository.UserRepository;
import com.cardealership.security.JwtService;
import com.cardealership.service.InventoryService;
import com.cardealership.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private InventoryService inventoryService;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User admin = userRepository.save(User.builder()
                .username("admin").email("admin@test.com")
                .password("pass1234").role(Role.ADMIN).build());
        User user = userRepository.save(User.builder()
                .username("user").email("user@test.com")
                .password("pass1234").role(Role.USER).build());
        adminToken = jwtService.generateToken(admin);
        userToken = jwtService.generateToken(user);
    }

    @Test
    void createVehicle_AsAdmin_ShouldReturn201() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        when(vehicleService.createVehicle(any())).thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "make", "Toyota", "model", "Camry", "category", "Sedan",
                                "price", 25000, "quantity", 10))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createVehicle_AsUser_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "make", "Toyota", "model", "Camry", "category", "Sedan",
                                "price", 25000, "quantity", 10))))
                .andExpect(status().isForbidden());
    }

    @Test
    void createVehicle_WithoutToken_ShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "make", "Toyota", "model", "Camry", "category", "Sedan",
                                "price", 25000, "quantity", 10))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getAllVehicles_ShouldReturn200() throws Exception {
        Page<VehicleResponse> page = Page.empty();
        when(vehicleService.getAllVehicles(any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void getAllVehicles_WithPagination_ShouldReturnPagedResponse() throws Exception {
        VehicleResponse vehicle = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        Page<VehicleResponse> page = new PageImpl<>(List.of(vehicle));
        when(vehicleService.getAllVehicles(any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles?page=0&size=10")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getVehicleById_WithValidId_ShouldReturn200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        when(vehicleService.getVehicleById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/vehicles/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getVehicleById_WithInvalidId_ShouldReturn404() throws Exception {
        when(vehicleService.getVehicleById(99L)).thenThrow(new ResourceNotFoundException("Vehicle not found"));

        mockMvc.perform(get("/api/vehicles/99")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchVehicles_ShouldReturnFilteredResults() throws Exception {
        VehicleResponse vehicle = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        Page<VehicleResponse> page = new PageImpl<>(List.of(vehicle));
        when(vehicleService.searchVehicles(any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles/search?make=Toyota")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].make").value("Toyota"));
    }

    @Test
    void searchVehicles_WithSorting_ShouldReturnSortedResults() throws Exception {
        VehicleResponse v1 = new VehicleResponse(1L, "BMW", "3 Series", "Sedan",
                new BigDecimal("43500"), 4);
        VehicleResponse v2 = new VehicleResponse(2L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 10);
        Page<VehicleResponse> page = new PageImpl<>(List.of(v1, v2));
        when(vehicleService.searchVehicles(any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/vehicles/search?sort=price,asc")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void updateVehicle_AsAdmin_ShouldReturn200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Accord", "Sedan",
                new BigDecimal("28000"), 8);
        when(vehicleService.updateVehicle(eq(1L), any())).thenReturn(response);

        mockMvc.perform(put("/api/vehicles/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "make", "Honda", "model", "Accord", "category", "Sedan",
                                "price", 28000, "quantity", 8))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.make").value("Honda"));
    }

    @Test
    void updateVehicle_AsUser_ShouldReturn403() throws Exception {
        mockMvc.perform(put("/api/vehicles/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "make", "Honda", "model", "Accord", "category", "Sedan",
                                "price", 28000, "quantity", 8))))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteVehicle_AsAdmin_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVehicle_AsUser_ShouldReturn403() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void purchaseVehicle_WithSufficientStock_ShouldReturn200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 7);
        when(inventoryService.purchaseVehicle(eq(1L), eq(1), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/vehicles/1/purchase")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(7));
    }

    @Test
    void purchaseVehicle_WithInsufficientStock_ShouldReturn400() throws Exception {
        when(inventoryService.purchaseVehicle(eq(1L), eq(1), any(User.class)))
                .thenThrow(new OutOfStockException("Insufficient stock"));

        mockMvc.perform(post("/api/vehicles/1/purchase")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 1))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void restockVehicle_AsAdmin_ShouldReturn200() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Toyota", "Camry", "Sedan",
                new BigDecimal("25000"), 15);
        when(inventoryService.restockVehicle(eq(1L), eq(5), any(User.class))).thenReturn(response);

        mockMvc.perform(post("/api/vehicles/1/restock")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 5))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(15));
    }

    @Test
    void restockVehicle_AsUser_ShouldReturn403() throws Exception {
        mockMvc.perform(post("/api/vehicles/1/restock")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("quantity", 5))))
                .andExpect(status().isForbidden());
    }
}