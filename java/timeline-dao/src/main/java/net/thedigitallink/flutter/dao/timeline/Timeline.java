package net.thedigitallink.flutter.dao.timeline;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.ClusteringColumn;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.*;

import java.util.UUID;

@CasquatchEntity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Timeline extends AbstractCasquatchEntity {
    @PartitionKey
    private String user;
    @ClusteringColumn(1)
    private String author;
    @ClusteringColumn(2)
    private Long createdDttm;
    private UUID messageId;
    private String message;
}



