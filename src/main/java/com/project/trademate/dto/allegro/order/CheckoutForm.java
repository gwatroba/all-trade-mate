package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckoutForm {
    private String id;
    private String status;
    private Buyer buyer;
    private Payment payment;
    private Fulfillment fulfillment;
    private List<LineItem> lineItems;
    private Instant updatedAt;
}