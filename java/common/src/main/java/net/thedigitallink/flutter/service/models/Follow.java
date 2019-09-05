package net.thedigitallink.flutter.service.models;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Follow extends AbstractEntity{
    private String follower;
    private String author;
}