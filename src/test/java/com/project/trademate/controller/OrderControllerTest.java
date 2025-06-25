package com.project.trademate.controller;

import com.project.trademate.dto.allegro.order.CheckoutForm;
import com.project.trademate.dto.allegro.order.Fulfillment;
import com.project.trademate.enums.OrderStatus;
import com.project.trademate.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    public void shouldReturnSentOrders() throws Exception {
        // Arrange
        CheckoutForm sentOrder = CheckoutForm.builder().id("order-sent-123")
                .fulfillment(Fulfillment.builder().status(OrderStatus.SENT).build())
                .build();

        when(orderService.getAllOrdersByStatus(OrderStatus.SENT)).thenReturn(List.of(sentOrder));

        mockMvc.perform(get("/orders").param("status", "SENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value("order-sent-123"));
    }

    @Test
    public void shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/orders").param("status", "NOT_EXISTS"))
                .andExpect(status().isBadRequest());
    }
}
