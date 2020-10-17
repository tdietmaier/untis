package at.dietmaier.untis.kafka;

import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.svc.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.ExecutionException;

public class KafkaProxy {
    @Autowired
    KafkaTemplate<Long,String> kafkaTemplate;

    public void send(QueueEntity toSend) throws ExecutionException, InterruptedException {
        SendResult<Long, String> result = kafkaTemplate.send("sample", toSend.getMessageId(), toSend.getMessageText())
                .completable()
                .get();

    }

    public void send(Message toSend) throws ExecutionException, InterruptedException {
        SendResult<Long, String> result = kafkaTemplate.send("sample", toSend.getId(), toSend.getText())
                .completable()
                .get();
    }
}
