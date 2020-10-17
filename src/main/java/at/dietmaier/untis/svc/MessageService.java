package at.dietmaier.untis.svc;

import at.dietmaier.untis.persistence.MessageEntity;
import at.dietmaier.untis.persistence.MessageRepository;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class MessageService {
    @Autowired
    private MessageRepository messageRepo;
    @Autowired
    private QueueRepository queueRepo;
    @Autowired
    private QueueService queueService;
    @Autowired
    private PlatformTransactionManager transactionManager;

    public MessageService() {
    }

    public void save(Message msg) {
        new TransactionTemplate(transactionManager).execute(status -> {
            MessageEntity msgEntity = new MessageEntity(msg.getId(), msg.getText());
            messageRepo.save(msgEntity);
            QueueEntity queue = new QueueEntity(msg.getId(), msg.getText());
            queueRepo.save(queue);
            return null;
        });
        queueService.processQueue();
    }
}
