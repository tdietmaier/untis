package at.dietmaier.untis;

import at.dietmaier.untis.api.MessageController;
import at.dietmaier.untis.kafka.KafkaProxy;
import at.dietmaier.untis.persistence.MessageEntity;
import at.dietmaier.untis.persistence.MessageRepository;
import at.dietmaier.untis.persistence.QueueEntity;
import at.dietmaier.untis.persistence.QueueRepository;
import at.dietmaier.untis.svc.Message;
import at.dietmaier.untis.svc.QueueService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { UntisApplication.class })
@WebAppConfiguration
@ActiveProfiles("test")
class UntisApplicationTests {

    @MockBean
    MessageRepository messageRepository;
    @Captor
    ArgumentCaptor<MessageEntity> msgCaptor;

    @MockBean
    QueueRepository queueRepository;
    @Captor
    ArgumentCaptor<QueueEntity> queueCaptor;

    @MockBean
    KafkaProxy kafkaProxy;
    @Captor
    ArgumentCaptor<QueueEntity> kafkaCaptor;

    @Autowired
    private MessageController controller;

    @Autowired
    private QueueService queueService;

    @Before
    public void setup() {
    }

    private void waitForAsyncProcessing() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendMessage_OK() {
        Message msg = new Message();
        msg.setId(29L);
        msg.setText("twenty-nine");

        ResponseEntity<String> result = controller.addMessage(msg);

        assertThat(result.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
        verify(messageRepository).save(msgCaptor.capture());
        assertThat(msgCaptor.getValue().getId(), is(29L));
        assertThat(msgCaptor.getValue().getMessage(), is("twenty-nine"));
        verify(queueRepository).save(queueCaptor.capture());
        assertThat(queueCaptor.getValue().getMessageId(), is(29L));
        assertThat(queueCaptor.getValue().getMessageText(), is("twenty-nine"));
    }

    @Test
    void sendKafka_OK() throws ExecutionException, InterruptedException {
        QueueEntity qe =new QueueEntity(37L, "siebenunddreißig");
        qe.setId(1037L);
        List<QueueEntity> qes = new ArrayList<>();
        qes.add(qe);
        when(queueRepository.findAll()).thenReturn(qes);

        queueService.processQueue();

        waitForAsyncProcessing();
        verify(kafkaProxy).send(kafkaCaptor.capture());
        assertThat(kafkaCaptor.getValue().getId(), is(1037L));
        verify(queueRepository).delete(queueCaptor.capture());
        assertThat(queueCaptor.getValue().getId(), is(1037L));
    }
}
