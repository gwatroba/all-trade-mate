package com.project.trademate.dto.allegro.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class MessageResponse {

    private String id;
    private String status;
    private String type;
    private Instant createdAt;
    private Thread thread;
    private Author author;
    private String text;
    private String subject;
    private RelatesTo relatesTo;
    private boolean hasAdditionalAttachments;
    private List<Attachment> attachments;
    private AdditionalInformation additionalInformation;


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Thread {
        private String id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Author {
        private String login;
        private boolean isInterlocutor;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class RelatesTo {
        private Object offer;
        private Object order;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Attachment {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class AdditionalInformation {
        private String vin;
    }
}
