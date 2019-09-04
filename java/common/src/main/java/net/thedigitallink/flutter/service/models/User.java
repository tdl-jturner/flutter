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
public class User extends AbstractEntity{
    private UUID id;
    private String username;
    private String email;
    private Boolean enableNotifications;
    long createdDttm;

    public User(String username, String email) {
        id=UUID.randomUUID();
        createdDttm=new Date().getTime();
    }
}
