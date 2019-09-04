package net.thedigitallink.flutter.dao.message;

import com.tmobile.opensource.casquatch.AbstractCasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.CasquatchEntity;
import com.tmobile.opensource.casquatch.annotation.ClusteringColumn;
import com.tmobile.opensource.casquatch.annotation.PartitionKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@CasquatchEntity
@Getter @Setter @NoArgsConstructor
public class MessageByAuthor extends AbstractCasquatchEntity {
    @PartitionKey
    private UUID author;
    @ClusteringColumn(1)
    private Long createdDttm;
    @ClusteringColumn(2)
    private UUID id;
    private String message;

    public MessageByAuthor(Message message) {
        this.author=message.getAuthor();
        this.createdDttm=message.getCreatedDttm();
        this.id = message.getId();
        this.message=message.getMessage();
    }
}



