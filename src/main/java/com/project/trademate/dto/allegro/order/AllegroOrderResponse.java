package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AllegroOrderResponse {
    private List<CheckoutForm> checkoutForms;
    private int count;
    private int totalCount;
}
