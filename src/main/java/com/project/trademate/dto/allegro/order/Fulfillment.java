package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.trademate.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class Fulfillment {

    private OrderStatus status;
    private ShipmentSummary shipmentSummary;
}
