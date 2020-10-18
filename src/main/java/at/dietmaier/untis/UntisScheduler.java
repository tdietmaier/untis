package at.dietmaier.untis;

import at.dietmaier.untis.svc.QueueService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UntisScheduler {

    private final QueueService queueService;

    public UntisScheduler(QueueService queueService) {
        this.queueService = queueService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendQueuedMessages() {
        queueService.processQueue();
    }

}
