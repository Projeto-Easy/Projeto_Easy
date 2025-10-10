package com.squad10.chatboteasy.dto;

import lombok.Getter;

@Getter
public class SendMessageRequest {
    private final String messaging_product = "whatsapp";
    private final String type = "text";
    private String to;
    private TextMessage text;

    public SendMessageRequest(String to, String text) {
        this.to = to;
        this.text = new TextMessage(text);
    }

    @Getter
    public static class TextMessage {
        private String body;

        public TextMessage(String body) {
            this.body = body;
        }

    }
}