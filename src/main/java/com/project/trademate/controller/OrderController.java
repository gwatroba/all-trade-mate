package com.project.trademate.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.trademate.client.allegro.AllegroApiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class OrderController {
    private final AllegroApiClient apiClient;
    private final ObjectMapper objectMapper;

    public OrderController(AllegroApiClient apiClient, ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getMyOrders() {
        String ordersUrl = "https://api.allegro.pl/order/checkout-forms?limit=3";
        try {
            String responseJson = apiClient.get(ordersUrl);
            JsonNode jsonNode = objectMapper.readTree(responseJson);
            return ResponseEntity.ok(jsonNode);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}