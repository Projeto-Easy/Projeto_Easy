package com.squad10.chatboteasy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class IncomingMessage {

    @JsonProperty("object")
    private String object;

    private List<Entry> entry;

    @Data
    public static class Entry {
        private String id;
        private List<Change> changes;
    }

    @Data
    public static class Change {
        private String field;
        private Value value;
    }

    @Data
    public static class Value {
        @JsonProperty("messaging_product")
        private String messagingProduct;

        private Metadata metadata;
        private List<Contact> contacts;
        private List<Message> messages;
    }

    @Data
    public static class Metadata {
        @JsonProperty("display_phone_number")
        private String displayPhoneNumber;

        @JsonProperty("phone_number_id")
        private String phoneNumberId;
    }

    @Data
    public static class Contact {
        private Profile profile;
        private String wa_id;
    }

    @Data
    public static class Profile {
        private String name;
    }

    @Data
    public static class Message {
        private String from;
        private String id;
        private long timestamp;
        private String type;
        private Text text;

        // Possivelmente adicionar outros tipos
    }

    @Data
    public static class Text {
        private String body;
    }
}