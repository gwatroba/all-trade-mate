package com.project.trademate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.dto.allegro.message.MessageResponse;
import com.project.trademate.dto.allegro.message.SendMessageRequest;
import com.project.trademate.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private AllegroApiClient apiClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(apiClient);
    }

    @Test
    void shouldCallApiClientWithCorrectUrl() throws IOException {
        String messageId = "test-message-id-123";
        String expectedUrl = "https://api.allegro.pl/messaging/messages/" + messageId;

        when(apiClient.get(any(), eq(MessageResponse.class))).thenReturn(new MessageResponse());

        orderService.getMessageDetails(messageId);

        verify(apiClient).get(eq(expectedUrl), eq(MessageResponse.class));
    }

    @Test
    void shouldCallPostWithCorrectlyConstructedBody() throws IOException {
        // Arrange
        String orderId = "order-id-456";
        String userLogin = "testUser";
        String expectedUrl = "https://api.allegro.pl/messaging/messages";
        String expectedContentType = "application/vnd.allegro.public.v1+json";

        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        when(apiClient.post(any(), any(), eq(MessageResponse.class), any())).thenReturn(new MessageResponse());

        orderService.sendThankYouMessage(orderId, userLogin);

        verify(apiClient).post(eq(expectedUrl), requestCaptor.capture(), eq(MessageResponse.class), eq(expectedContentType));

        SendMessageRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getRecipient().getLogin()).isEqualTo(userLogin);
        assertThat(capturedRequest.getOrder().getId()).isEqualTo(orderId);
        assertThat(capturedRequest.getText()).contains(String.format("https://allegro.pl/moje-allegro/zakupy/ocen-sprzedawce?orderId=%s", orderId));
    }
}