package com.project.trademate.controller;

import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.service.MessageService;
import com.project.trademate.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.trademate.enums.OrderStatus.PICKED_UP;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;
    @MockBean
    private MessageService messageService;

    @Test
    public void shouldReturnAllOrders() throws Exception {
        // given
        CheckoutForm sentOrderOne = new CheckoutForm();
        sentOrderOne.setId("sent-order-123");
        CheckoutForm sentOrderTwo = new CheckoutForm();
        sentOrderTwo.setId("sent-order-456");

        when(orderService.getAllOrdersByStatusOlderThan(OrderStatus.SENT, 0))
                .thenReturn(List.of(sentOrderOne, sentOrderTwo));
        when(orderService.getAllOrdersByStatusOlderThan(OrderStatus.SENT, 7))
                .thenReturn(List.of(sentOrderTwo));

        //then
        mockMvc.perform(get("/api/orders")
                        .param("status", "SENT")
                        .with(httpBasic("testuser", "testpassword")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("sent-order-123"))
                .andExpect(jsonPath("$[1].id").value("sent-order-456"));
    }

    @Test
    public void shouldReturnAllOrdersOlderThan() throws Exception {
        // given
        CheckoutForm sentOrderOne = new CheckoutForm();
        sentOrderOne.setId("sent-order-123");
        CheckoutForm sentOrderTwo = new CheckoutForm();
        sentOrderTwo.setId("sent-order-456");

        when(orderService.getAllOrdersByStatusOlderThan(OrderStatus.SENT, 0))
                .thenReturn(List.of(sentOrderOne, sentOrderTwo));
        when(orderService.getAllOrdersByStatusOlderThan(OrderStatus.SENT, 7))
                .thenReturn(List.of(sentOrderTwo));

        //then
        mockMvc.perform(get("/api/orders")
                        .param("status", "SENT")
                        .param("ageInDays", "7")
                        .with(httpBasic("testuser", "testpassword")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("sent-order-456"));
    }

    @Test
    public void shouldReturnUnauthorized() throws Exception {
        // given
        CheckoutForm sentOrder = new CheckoutForm();
        sentOrder.setId("sent-order-123");

        when(orderService.getAllOrdersByStatusOlderThan(OrderStatus.SENT, 0))
                .thenReturn(List.of(sentOrder));
        //then
        mockMvc.perform(get("/api/orders")
                        .param("status", "SENT")
                        .with(httpBasic("user", "wrongPass")))
                .andExpect(status().isUnauthorized());
    }
}
