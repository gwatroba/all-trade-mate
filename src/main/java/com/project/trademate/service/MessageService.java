package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.message.SendMessageRequest;
import com.project.trademate.exception.MessagingApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.project.trademate.dto.allegro.message.SendMessageRequest.createThankYouRateMessage;

@Slf4j
@Service
public class MessageService {
    private final AllegroApiClient apiClient;

    public MessageService(AllegroApiClient apiClient) {
        this.apiClient = apiClient;
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
