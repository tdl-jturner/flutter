package net.thedigitallink.flutter.dao.follow;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@CasquatchEntity
@Getter @Setter @NoArgsConstructor
public class Follow extends AbstractCasquatchEntity {
    @PartitionKey
    private String follower;
    private String author;
}



