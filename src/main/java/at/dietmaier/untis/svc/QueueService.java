package at.dietmaier.untis.svc;

import at.dietmaier.untis.kafka.KafkaProxy;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import lombok.Synchronized;
import org.slf4j.Logger;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class QueueService {
    private final QueueRepository queueRepo;
    private final Logger log;
    private final KafkaProxy kafka;
    private final TaskExecutor taskExecutor;

    /** indicates if a doProcessQueue invocation is already pending */
    private final AtomicBoolean processQueuePending = new AtomicBoolean(false);

    public QueueService(QueueRepository queueRepo, Logger log, KafkaProxy kafka, TaskExecutor taskExecutor) {
        this.queueRepo = queueRepo;
        this.log = log;
        this.kafka = kafka;
        this.taskExecutor = taskExecutor;
    }

    public void processQueue() {
        if(processQueuePending.getAndSet(true)) {
            log.debug("doProcessQueue is already pending");
        } else {
            taskExecutor.execute(this::doProcessQueue);
        }
    }

    @Synchronized
    protected void doProcessQueue() {
        try {
            processQueuePending.set(false);
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
