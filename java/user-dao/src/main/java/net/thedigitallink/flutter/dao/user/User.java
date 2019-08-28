package net.thedigitallink.flutter.dao.user;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@CasquatchEntity
@Getter @Setter @NoArgsConstructor
public class User extends AbstractCasquatchEntity {
    @PartitionKey
    private UUID id;
    private String username;
    private String email;
    private Boolean enableNotifications;
    private Long createdDttm;
}



