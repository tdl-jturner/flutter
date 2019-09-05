package net.thedigitallink.flutter.service.models;

import lombok.*;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Timeline extends AbstractEntity{
    private String user;
    private String author;
    private Long createdDttm=new Date().getTime();;
    private UUID messageId;
    private String message;
}