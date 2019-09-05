package net.thedigitallink.flutter.service.models;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity{
    private String username;
    private String email;
    private Boolean enableNotifications;
    long createdDttm=new Date().getTime();
}
