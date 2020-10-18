package at.dietmaier.untis.svc;

public interface QueueService {
    /**
     * send all queued messages to kafka.
     */
    void processQueue();
}
