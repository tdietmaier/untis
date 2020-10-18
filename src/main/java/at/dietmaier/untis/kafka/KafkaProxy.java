package at.dietmaier.untis.kafka;

import at.dietmaier.untis.persistence.QueueEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class KafkaProxy {
    private final KafkaTemplate<Long,String> kafkaTemplate;
    private final Logger log;

    public KafkaProxy(KafkaTemplate<Long, String> kafkaTemplate, Logger log) {
        this.kafkaTemplate = kafkaTemplate;
        this.log = log;
    }

    public void send(QueueEntity toSend) throws ExecutionException, InterruptedException {
        SendResult<Long, String> result = kafkaTemplate.send("sample", toSend.getMessageId(), toSend.getMessageText())
                .completable()
                .get();
        log.debug("send result: {}", result);

    }

}
