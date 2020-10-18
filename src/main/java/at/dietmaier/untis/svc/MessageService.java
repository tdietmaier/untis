package at.dietmaier.untis.svc;

import at.dietmaier.untis.persistence.MessageEntity;
import at.dietmaier.untis.persistence.MessageRepository;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class MessageService {
    private final MessageRepository messageRepo;
    private final QueueRepository queueRepo;
    private final QueueService queueService;
    private final PlatformTransactionManager transactionManager;

    public MessageService(MessageRepository messageRepo, QueueRepository queueRepo, QueueService queueService, PlatformTransactionManager transactionManager) {
        this.messageRepo = messageRepo;
        this.queueRepo = queueRepo;
        this.queueService = queueService;
        this.transactionManager = transactionManager;
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
