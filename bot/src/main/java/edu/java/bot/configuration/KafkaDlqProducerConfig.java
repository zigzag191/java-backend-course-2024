package edu.java.bot.configuration;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;

@Configuration
@Log4j2
public class KafkaDlqProducerConfig {

    @Bean
    public NewTopic dlqTopic(ApplicationConfig config) {
        return TopicBuilder.name(config.kafkaDlqProducerConfig().dlqTopicName())
            .partitions(1)
            .build();
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory(ApplicationConfig config) {
        return new DefaultKafkaProducerFactory<>(senderProps(config.kafkaDlqProducerConfig()));
    }

    private Map<String, Object> senderProps(ApplicationConfig.KafkaDlqProducerConfig config) {
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ProducerConfig.LINGER_MS_CONFIG, config.lingerMs());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> dlqKafkaTemplate(
        ProducerFactory<Integer, String> producerFactory,
        ApplicationConfig config
    ) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(config.kafkaDlqProducerConfig().dlqTopicName());
        return kafkaTemplate;
    }

    @Bean
    public KafkaListenerErrorHandler dlqExceptionHandler(KafkaTemplate<Integer, String> dlqKafkaTemplate) {
        return (msg, ex) -> {
            log.error("Kafka listener error: {}", ex.getMessage());
            dlqKafkaTemplate.send(msg);
            return msg;
        };
    }

}
