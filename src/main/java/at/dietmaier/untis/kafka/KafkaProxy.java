package at.dietmaier.untis.kafka;

import at.dietmaier.untis.persistence.QueueEntity;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Configuration
public class KafkaProxy {
    @Value("${untis.kafka.topic}")
    private String topic;
    private final KafkaTemplate<Long,String> kafkaTemplate;
    private final Logger log;

    public KafkaProxy(KafkaTemplate<Long, String> kafkaTemplate, Logger log) {
        this.kafkaTemplate = kafkaTemplate;
        this.log = log;
    }

    /**
     * Send the given queue entry to kafka. Sending has succeeded if no exception is thrown
     * Exceptions are thrown directly from the (spring) kafkaTemplate
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void send(QueueEntity toSend) throws ExecutionException, InterruptedException {
        SendResult<Long, String> result = kafkaTemplate.send(topic, toSend.getMessageId(), toSend.getMessageText())
                .completable()
                .get();
        log.debug("send result: {}", result);

    }

}
