package net.thedigitallink.flutter.service.models;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message extends AbstractEntity{
    private UUID id=UUID.randomUUID();
    private String author;
    private String message;
    private Long createdDttm=new Date().getTime();
}