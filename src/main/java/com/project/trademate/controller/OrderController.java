package com.project.trademate.controller;

import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.service.MessageService;
import com.project.trademate.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Slf4j
@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final MessageService messageService;

    public OrderController(OrderService orderService, MessageService messageService) {
        this.orderService = orderService;
        this.messageService = messageService;
    }

    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@RequestParam() OrderStatus status, @RequestParam(required = false, defaultValue = "0") int ageInDays) {
        try {
            List<CheckoutForm> orders;
            log.info("CONTROLLER: Fetching orders with status: {}", status.name());
            orders = orderService.getAllOrdersByStatusOlderThan(status, ageInDays);
            return ResponseEntity.ok(orders);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/orders/message")
    public ResponseEntity<?> sentThankYouMessageForStatus(@RequestParam() OrderStatus status, @RequestParam() int minusDays)
            throws InterruptedException {
        List<MessageResponse> responses = new ArrayList<>();

        List<CheckoutForm> orders;
        try {
            orders = orderService.getAllOrdersByStatusOlderThan(status, minusDays);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error fetching orders: " + e.getMessage());
        }

        for (CheckoutForm order : orders) {
            log.info("Sending message to: {} for orderId: {}", order.getId(), order.getBuyer().getLogin());
            MessageResponse messageResponse = messageService.sendThankYouMessage(order.getId(), order.getBuyer().getLogin());
            orderService.setFulfillmentStatus(order.getId(), OrderStatus.PICKED_UP);
            responses.add(messageResponse);
            Thread.sleep(3000);
        }
        return ResponseEntity.ok(responses);
    }
}