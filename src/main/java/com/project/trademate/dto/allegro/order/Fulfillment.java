package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Fulfillment {

    private String status;
    private ShipmentSummary shipmentSummary;
}
