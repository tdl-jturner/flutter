package net.thedigitallink.flutter.dao.message;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@CasquatchEntity
@Getter @Setter @NoArgsConstructor
public class Message extends AbstractCasquatchEntity {
    @PartitionKey
    private UUID id;
    private UUID author;
    private String message;
    private Long createdDttm;
}



