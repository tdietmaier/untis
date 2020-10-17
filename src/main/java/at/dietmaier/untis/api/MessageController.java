package at.dietmaier.untis.api;

import at.dietmaier.untis.svc.Message;
import at.dietmaier.untis.svc.MessageService;
import at.dietmaier.untis.svc.XaMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private XaMessageService xaMessageService;

    @PostMapping("/messages")
    public void addMessage(@RequestBody Message msg) {
        messageService.save(msg);
    }

    @PostMapping("/messagesXa")
    public void addMessageXa(@RequestBody Message msg) {
        xaMessageService.save(msg);
    }
}
