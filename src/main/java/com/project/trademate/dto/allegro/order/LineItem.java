package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LineItem {
    private String id;
    private int quantity;
    private Price price;
    private Offer offer;
}
