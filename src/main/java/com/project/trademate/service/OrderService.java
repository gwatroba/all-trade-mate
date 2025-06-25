package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.message.SendMessageRequest;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.exception.MessagingApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

import static com.project.trademate.dto.allegro.message.SendMessageRequest.createThankYouRateMessage;

@Slf4j
@Service
public class OrderService {
    private final AllegroApiClient apiClient;
    private static final String ALLEGRO_V1_CONTENT_TYPE = "application/vnd.allegro.public.v1+json";

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
            MessageResponse response = apiClient.post(sendMessageUrl, requestBody, MessageResponse.class, ALLEGRO_V1_CONTENT_TYPE);
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
