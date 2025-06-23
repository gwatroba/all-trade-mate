package com.project.trademate.controller;

import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<CheckoutForm>> getOrders() {
        String ordersUrl = "https://api.allegro.pl/order/checkout-forms?limit=3";
        orderService.getAllOrders();
        List<CheckoutForm> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/orders/sent")
    public ResponseEntity<List<CheckoutForm>> getSentOrders() {
        try {
            List<CheckoutForm> sentOrders = orderService.getAllOrdersByStatus("SENT");
            return ResponseEntity.ok(sentOrders);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/orders/sent/message")
    public MessageResponse sentThankYouMessageForSentOrders() throws InterruptedException {
        // get ids and login sent orders
        MessageResponse response;
        try {
            response = orderService.sendThankYouMessage("a2bdb280-3d7e-11f0-9519-cd725f92e9f4", "karolina1_1k");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Thread.sleep(60000);// chwile to trwa
        try {
            response = orderService.getMessageDetails(response.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}