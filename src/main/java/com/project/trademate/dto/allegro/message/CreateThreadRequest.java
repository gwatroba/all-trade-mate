package com.project.trademate.dto.allegro.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for creating a new message thread.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateThreadRequest {

    private Participant participant;
    private Context context;

    // Represents the 'participant' part of the request.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Participant {
        private String login;
    }

    // Represents the 'context' part of the request, linking it to an order.
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Context {
        private String type; // e.g., "checkoutForm"
        private String id;   // e.g., the order ID
    }
}