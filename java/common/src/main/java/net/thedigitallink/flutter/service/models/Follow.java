package net.thedigitallink.flutter.service.models;

import lombok.*;

import java.util.UUID;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Follow extends AbstractEntity{
    private UUID follower;
    private UUID author;
}