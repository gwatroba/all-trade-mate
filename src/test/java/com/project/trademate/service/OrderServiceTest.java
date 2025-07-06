package com.project.trademate.service;

import com.project.trademate.client.allegro.AllegroApiClient;
import com.project.trademate.dto.allegro.order.AllegroOrderResponse;
import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.enums.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
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
    public void shouldGetAllOrdersByStatus() throws IOException {
        OrderStatus statusToTest = OrderStatus.SENT;
        String expectedUrl = "https://api.allegro.pl/order/checkout-forms?fulfillment.status=SENT";

        CheckoutForm testOrder = new CheckoutForm();
        testOrder.setId("order-123");

        AllegroOrderResponse apiResponse = new AllegroOrderResponse();
        apiResponse.setCheckoutForms(List.of(testOrder));

        when(apiClient.get(eq(expectedUrl), eq(AllegroOrderResponse.class)))
                .thenReturn(apiResponse);

        List<CheckoutForm> result = orderService.getAllOrdersByStatus(statusToTest);

        assertThat(result)
                .isNotNull()
                .hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("order-123");

        verify(apiClient).get(eq(expectedUrl), eq(AllegroOrderResponse.class));
    }

}