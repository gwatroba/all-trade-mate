package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CheckoutForm {
    private String id;
    private String status;
    private Buyer buyer;
    private Payment payment;
    private Fulfillment fulfillment;
    private List<LineItem> lineItems;
    // You can add more fields here later, e.g., Delivery delivery;
}