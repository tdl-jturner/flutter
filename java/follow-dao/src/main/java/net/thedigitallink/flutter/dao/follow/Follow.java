package net.thedigitallink.flutter.dao.follow;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@CasquatchEntity
@Getter @Setter @NoArgsConstructor
public class Follow extends AbstractCasquatchEntity {
    @PartitionKey
    private UUID follower;
    private UUID author;
}



