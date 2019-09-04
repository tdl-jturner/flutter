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
public class Message extends AbstractEntity{
    private UUID id;
    private UUID author;
    private String message;
    private Long createdDttm;

    public Message(UUID author, String message) {
        id=UUID.randomUUID();
        createdDttm=new Date().getTime();
    }
}