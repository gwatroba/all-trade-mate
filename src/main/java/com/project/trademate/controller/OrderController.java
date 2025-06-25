package com.project.trademate.controller;

import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@RequestParam() OrderStatus status) {
        try {
            List<CheckoutForm> orders;
            log.info("CONTROLLER: Fetching orders with status: {}", status.name());
            orders = orderService.getAllOrdersByStatus(status);
            return ResponseEntity.ok(orders);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/orders/sent/message")
    public MessageResponse sentThankYouMessageForStatus() {
        // get ids and login sent orders
        MessageResponse response;

        response = orderService.sendThankYouMessage("a2bdb280-3d7e-11f0-9519-cd725f92e9f4", "karolina1_1k");
        response = orderService.getMessageDetails(response.getId());

        return response;
    }
}