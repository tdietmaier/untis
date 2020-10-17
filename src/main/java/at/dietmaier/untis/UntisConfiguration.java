package at.dietmaier.untis;

import at.dietmaier.untis.kafka.KafkaProxy;
import at.dietmaier.untis.svc.MessageService;
import at.dietmaier.untis.svc.QueueService;
import at.dietmaier.untis.svc.XaMessageService;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;
import javax.sql.XADataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableConfigurationProperties
@EnableAutoConfiguration
public class UntisConfiguration {

    @Bean
    public MessageService messageService() {
        return new MessageService();
    }
    @Bean
    public XaMessageService xaMessageService() {
        return new XaMessageService();
    }

    @Bean
    public QueueService queueService() {
        return new QueueService();
    }

    @Bean
    public KafkaProxy kafkaProxy() {
        return new KafkaProxy();
    }

    @Bean
    public KafkaTemplate<Long,String> kafkaTemplate() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(configProps));
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendQueuedMessages() {
        queueService().processQueue();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger logger(InjectionPoint injectionPoint){
        return LoggerFactory.getLogger(injectionPoint.getField().getDeclaringClass());
    }

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource.untis")
    public AtomikosDataSourceBean xaDataSource() {
        return new AtomikosDataSourceBean();
    }
}
