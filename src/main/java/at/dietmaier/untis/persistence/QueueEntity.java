package at.dietmaier.untis.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueueEntity {
    @Id
    @GeneratedValue
    private long id;

    private long messageId;

    @Size(max=200)
    private String messageText;

    public QueueEntity(long messageid, String messageText) {
        this.messageId = messageid;
        this.messageText = messageText;
    }
}
