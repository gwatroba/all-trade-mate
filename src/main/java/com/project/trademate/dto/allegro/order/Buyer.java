package com.project.trademate.dto.allegro.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Buyer {
    private String id;
    private String email;
    private String login;
    private String firstName;
    private String lastName;
}