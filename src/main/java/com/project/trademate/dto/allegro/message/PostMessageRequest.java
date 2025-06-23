package com.project.trademate.dto.allegro.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// DTO for the body of the request to post a message into an existing thread.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostMessageRequest {

    private String text;

    // In the future, if we wanted to handle attachments, we would add them here.
    // private java.util.List<Attachment> attachments;
}