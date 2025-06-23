package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.message.SendMessageRequest;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.project.trademate.dto.allegro.message.SendMessageRequest.createThankYouRateMessage;

@Service
public class OrderService {
    private final AllegroApiClient apiClient;
    private static final String ALLEGRO_V1_CONTENT_TYPE = "application/vnd.allegro.public.v1+json";

    public OrderService(AllegroApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public List<CheckoutForm> getAllOrders() {
        String url = "https://api.allegro.pl/order/checkout-forms";
        try {
            return apiClient.get(url, AllegroOrderResponse.class).getCheckoutForms();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<CheckoutForm> getAllOrdersByStatus(String status) throws IOException {
        List<CheckoutForm> allOrders = new ArrayList<>();
        int limit = 100;
        int offset = 0;
        int totalCount;

        System.out.println("SERVICE: Starting to fetch all orders with status: " + status);

        do {
            String url = String.format(
                    "https://api.allegro.pl/order/checkout-forms?fulfillment.status=%s&limit=%d&offset=%d",
                    status, limit, offset
            );

            AllegroOrderResponse page = apiClient.get(url, AllegroOrderResponse.class);

            if (page != null && page.getCheckoutForms() != null) {
                allOrders.addAll(page.getCheckoutForms());
                totalCount = page.getTotalCount();
                offset += page.getCount();
            } else {
                // If the response is null or doesn't contain orders, stop the loop.
                totalCount = 0;
            }

        } while (offset < totalCount);

        System.out.println("SERVICE: Finished fetching. Total orders found: " + allOrders.size());
        return allOrders;
    }

    public MessageResponse sendThankYouMessage(String orderId, String userLogin) throws IOException {
        String messageText = createThankYouRateMessage(orderId);
        String sendMessageUrl = "https://api.allegro.pl/messaging/messages";

        SendMessageRequest.Recipient recipient = new SendMessageRequest.Recipient(userLogin);
        SendMessageRequest.Order orderContext = new SendMessageRequest.Order(orderId);
        SendMessageRequest requestBody = new SendMessageRequest(recipient, messageText, Collections.emptyList(), orderContext);

        return apiClient.post(sendMessageUrl, requestBody, MessageResponse.class, ALLEGRO_V1_CONTENT_TYPE);
    }

    public MessageResponse getMessageDetails(String messageId) throws IOException {
        System.out.println("SERVICE: Checking status for message ID: " + messageId);
        String messageDetailsUrl = "https://api.allegro.pl/messaging/messages/" + messageId;
        return apiClient.get(messageDetailsUrl, MessageResponse.class);
    }
}
