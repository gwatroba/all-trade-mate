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
    void getMessageDetails_shouldCallApiClientWithCorrectUrl() throws IOException {
        String messageId = "test-message-id-123";
        String expectedUrl = "https://api.allegro.pl/messaging/messages/" + messageId;

        when(apiClient.get(any(), eq(MessageResponse.class))).thenReturn(new MessageResponse());

        orderService.getMessageDetails(messageId);

        verify(apiClient).get(eq(expectedUrl), eq(MessageResponse.class));
    }

    @Test
    void sendThankYouMessage_shouldCallPostWithCorrectlyConstructedBody() throws IOException {
        // Arrange
        String orderId = "order-id-456";
        String userLogin = "testUser";
        String expectedUrl = "https://api.allegro.pl/messaging/messages";
        String expectedContentType = "application/vnd.allegro.public.v1+json";

        ArgumentCaptor<SendMessageRequest> requestCaptor = ArgumentCaptor.forClass(SendMessageRequest.class);
        when(apiClient.post(any(), any(), eq(MessageResponse.class), any())).thenReturn(null);

        orderService.sendThankYouMessage(orderId, userLogin);

        verify(apiClient).post(eq(expectedUrl), requestCaptor.capture(), eq(MessageResponse.class), eq(expectedContentType));

        SendMessageRequest capturedRequest = requestCaptor.getValue();
        assertThat(capturedRequest.getRecipient().getLogin()).isEqualTo(userLogin);
        assertThat(capturedRequest.getOrder().getId()).isEqualTo(orderId);
        assertThat(capturedRequest.getText()).contains(String.format("https://allegro.pl/moje-allegro/zakupy/ocen-sprzedawce?orderId=%s", orderId));
    }

    @Test
    void getAllOrdersByStatus_shouldHandlePagination() throws IOException {
        // Arrange
        String status = "SENT";
        // Mock the first API call to return a page with 2 orders, but a total count of 3.
        AllegroOrderResponse page1 = new AllegroOrderResponse();
        page1.setCheckoutForms(List.of(new CheckoutForm(), new CheckoutForm()));
        page1.setCount(2);
        page1.setTotalCount(3);
        when(apiClient.get(eq("https://api.allegro.pl/order/checkout-forms?fulfillment.status=SENT&limit=100&offset=0"), eq(AllegroOrderResponse.class)))
                .thenReturn(page1);

        // Mock the second API call (with offset=100) to return the final page with 1 order.
        AllegroOrderResponse page2 = new AllegroOrderResponse();
        page2.setCheckoutForms(List.of(new CheckoutForm()));
        page2.setCount(1);
        page2.setTotalCount(3);
        when(apiClient.get(eq("https://api.allegro.pl/order/checkout-forms?fulfillment.status=SENT&limit=100&offset=100"), eq(AllegroOrderResponse.class)))
                .thenReturn(page2);

        // Act
        List<CheckoutForm> result = orderService.getAllOrdersByStatus(OrderStatus.SENT);

        // Assert
        // Check that the final list contains all 3 orders from both pages.
        assertThat(result).hasSize(3);
        // Verify that the apiClient.get method was called exactly twice.
        verify(apiClient, org.mockito.Mockito.times(2)).get(any(), eq(AllegroOrderResponse.class));
    }
}