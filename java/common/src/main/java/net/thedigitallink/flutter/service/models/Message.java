package net.thedigitallink.flutter.service.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private UUID id;
    private UUID author;
    private String message;
    private Long createdDttm;

    public Message(UUID author, String message) {
        id=UUID.randomUUID();
        createdDttm=new Date().getTime();
    }
    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Unable to render JSON.";
        }
    }
    public static Message fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json,Message.class);
    }
}