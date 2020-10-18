package at.dietmaier.untis.svc;

public interface MessageService {
    /**
     * save the given message to the db, and trigger sending to kafka
     */
    void save(Message msg);
}
