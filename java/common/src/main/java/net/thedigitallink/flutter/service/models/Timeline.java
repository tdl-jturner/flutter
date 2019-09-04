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
public class Timeline extends AbstractEntity{
    private UUID user;
    private UUID author;
    private Long createdDttm;
    private UUID message;
}