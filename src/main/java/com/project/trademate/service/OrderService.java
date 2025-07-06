package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.dto.allegro.order.Fulfillment;
import com.project.trademate.enums.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
public class OrderService {
    private final AllegroApiClient apiClient;

    public OrderService(AllegroApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<CheckoutForm> getAllOrdersByStatus(OrderStatus status) throws IOException {
        log.info("SERVICE: Starting to fetch all orders with status: {}", status);

        String url = String.format("https://api.allegro.pl/order/checkout-forms?fulfillment.status=%s", status);

        List<CheckoutForm> allOrders = apiClient.get(url, AllegroOrderResponse.class).getCheckoutForms();

        log.info("SERVICE: Finished fetching. Total orders found: {}", allOrders.size());
        return allOrders;
    }

    public List<CheckoutForm> getAllOrdersByStatusOlderThan(OrderStatus status, int minusDays) throws IOException {
        List<CheckoutForm> allOrders = getAllOrdersByStatus(status);
        return allOrders.stream()
                .filter(checkoutForm -> checkoutForm.getLineItems().get(0).getBoughtAt()
                        .isBefore(Instant.now().minus(minusDays, ChronoUnit.DAYS)))
                .toList();
    }

    public CheckoutForm setFulfillmentStatus(String orderId, OrderStatus status) {
        log.info("SERVICE: Setting order: {} with status: {}", orderId, status);

        String url = String.format("https://api.allegro.pl/order/checkout-forms/%s/fulfillment", orderId);
        Fulfillment fulfillment = Fulfillment.builder().status(status).build();

        CheckoutForm updatedOrder;
        try {
            updatedOrder = apiClient.put(url, fulfillment, CheckoutForm.class);
        } catch (IOException e) {
            log.error("Failed to update orderId: {}", orderId, e);
            throw new RuntimeException("Failed update order " + orderId, e);
        }

        log.info("SERVICE: Finished update. Response: {}", updatedOrder);
        return updatedOrder;
    }
}
