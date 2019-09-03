package net.thedigitallink.flutter.service.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Timeline {
    private UUID user;
    private UUID author;
    private Long createdDttm;
    private UUID message;

    public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Unable to render JSON.";
        }
    }
    public static Timeline fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json, Timeline.class);
    }
}