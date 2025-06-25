package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.message.SendMessageRequest;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.dto.allegro.order.Fulfillment;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.exception.MessagingApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.project.trademate.dto.allegro.message.SendMessageRequest.createThankYouRateMessage;

@Slf4j
@Service
public class OrderService {
    private final AllegroApiClient apiClient;

    public OrderService(AllegroApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<CheckoutForm> getAllOrders() throws IOException {
        log.info("SERVICE: Starting to fetch all orders");

        String url = "https://api.allegro.pl/order/checkout-forms";

        List<CheckoutForm> allOrders = apiClient.get(url, AllegroOrderResponse.class).getCheckoutForms();

        log.info("SERVICE: Finished fetching. Total orders found: {}", allOrders.size());
        return allOrders;
    }

    // TODO: add tests for that method
    public List<CheckoutForm> getAllOrdersByStatusOlderThanTenDays(OrderStatus status) throws IOException {
        log.info("SERVICE: Starting to fetch all orders with status: {}", status);

        String url = String.format("https://api.allegro.pl/order/checkout-forms?fulfillment.status=%s", status);

        List<CheckoutForm> allOrders = apiClient.get(url, AllegroOrderResponse.class).getCheckoutForms();

        log.info("SERVICE: Finished fetching. Total orders found: {}", allOrders.size());
        return allOrders.stream()
                .filter(checkoutForm -> checkoutForm.getLineItems().get(0).getBoughtAt()
                        .isBefore(Instant.now().minus(10, ChronoUnit.DAYS)))
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

    public MessageResponse sendThankYouMessage(String orderId, String userLogin) {
        String messageText = createThankYouRateMessage(orderId);
        String sendMessageUrl = "https://api.allegro.pl/messaging/messages";

        log.info("Preparing to send a thank-you message for orderId: {}", orderId);

        SendMessageRequest requestBody = SendMessageRequest.builder()
                .recipient(SendMessageRequest.Recipient.builder().login(userLogin).build())
                .order(SendMessageRequest.Order.builder().id(orderId).build())
                .text(messageText)
                .build();
        try {
            MessageResponse response = apiClient.post(sendMessageUrl, requestBody, MessageResponse.class);
            log.info("Successfully sent message for orderId: {}. MessageId: {}", orderId, response.getId());
            return response;
        } catch (IOException e) {
            log.error("Failed to send message for orderId: {}", orderId, e);
            throw new MessagingApiException("Failed to send message for order " + orderId, e);
        }
    }

    public MessageResponse getMessageDetails(String messageId) {
        log.info("SERVICE: Checking status for message ID: {}", messageId);
        String messageDetailsUrl = "https://api.allegro.pl/messaging/messages/" + messageId;

        try {
            return apiClient.get(messageDetailsUrl, MessageResponse.class);
        } catch (IOException e) {
            log.error("Failed to get details for messageId: {}", messageId, e);
            throw new MessagingApiException("Failed to get details for message " + messageId, e);
        }
    }
}
