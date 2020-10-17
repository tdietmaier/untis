package at.dietmaier.untis.svc;

import at.dietmaier.untis.kafka.KafkaProxy;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QueueService {
    @Autowired
    QueueRepository queueRepo;
    @Autowired
    Logger log;
    @Autowired
    KafkaProxy kafka;
    @Autowired
    private TaskExecutor taskExecutor;

    private final AtomicInteger waiting = new AtomicInteger(0);

    public QueueService() {
    }

    public void processQueue() {
        if(waiting.getAndAdd(1) == 0) {
            taskExecutor.execute(() -> doProcessQueue());
        } else {
            log.info("doProcessQueue is already pending");
        }
    }

    @Synchronized
    protected void doProcessQueue() {
        try {
            waiting.set(0);
            List<QueueEntity> unsent = queueRepo.findAll();
            log.info("about to send {} queued messages", unsent.size());
            for (QueueEntity toSend : unsent) {
                try {
                    // send synchronously; we need to delete the processed entities
                    // before the next invocation of processQueue()
                    kafka.send(toSend);
                    queueRepo.delete(toSend);
                } catch (Exception ex) {
                    log.warn("failed to process queue entry, will retry " + toSend, ex);
                }
            }
            log.info("done sending");
        } catch(Exception ex) {
            log.warn("error while processing queue ",ex);
        }
    }
}
