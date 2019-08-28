package net.thedigitallink.flutter.service.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class User {
    private UUID id;
    private String username;
    private String email;
    private Boolean enableNotifications;
    long createdDttm;

    public User(String username, String email) {
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
    public static User fromJson(String json) throws IOException {
        return new ObjectMapper().readValue(json,User.class);
    }
}