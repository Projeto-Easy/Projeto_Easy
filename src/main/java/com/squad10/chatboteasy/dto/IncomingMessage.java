package com.squad10.chatboteasy.dto;

import lombok.Data;
import java.util.List;

@Data
public class IncomingMessage {

    private String Object;
    private List<Entry> entry;

    @Data
    public static class Entry{
        private String id;
        private List<Changes> changes;
    }

    @Data
    public static class Changes{
        private String field;
        private Value value;
    }

    @Data
    public static class Value{
        private List<Message> messages;
    }

    @Data
    public static class Message{
        private String from;
        private String id;
        private String timestamp;
        private String type;
        private Text text;
    }

    @Data
    public static class Text{
        private String body;
    }

}
