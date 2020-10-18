package at.dietmaier.untis.api;

import at.dietmaier.untis.svc.Message;
import at.dietmaier.untis.svc.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/messages")
    public ResponseEntity<String> addMessage(@Valid @RequestBody Message msg) {
        messageService.save(msg);
        // msg might not have been sent to kafka yet => status 202 seems appropriate
        return ResponseEntity.accepted().build();
    }
}
