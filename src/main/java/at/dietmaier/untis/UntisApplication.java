package at.dietmaier.untis;

import at.dietmaier.untis.svc.MessageService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class UntisApplication {

    public static void main(String[] args) {
        SpringApplication.run(UntisApplication.class, args);
    }

}
