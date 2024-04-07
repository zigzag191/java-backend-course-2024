package edu.java.scrapper.configuration;

import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaUpdatesProducerConfig {

    @Bean
    public NewTopic updatesTopic(ApplicationConfig config) {
        return TopicBuilder.name(config.kafkaProducerConfig().updatesTopicName())
            .partitions(1)
            .build();
    }

    @Bean
    public ProducerFactory<Integer, LinkUpdateRequest> producerFactory(ApplicationConfig config) {
        return new DefaultKafkaProducerFactory<>(senderProps(config.kafkaProducerConfig()));
    }

    private Map<String, Object> senderProps(ApplicationConfig.KafkaProducerConfig config) {
        var props = new HashMap<String, Object>();
        props.put(ProducerConfig.ACKS_CONFIG, config.acknowledgments());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ProducerConfig.LINGER_MS_CONFIG, config.lingerMs());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, LinkUpdateRequest> kafkaTemplate(
        ProducerFactory<Integer, LinkUpdateRequest> producerFactory,
        ApplicationConfig config
    ) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(config.kafkaProducerConfig().updatesTopicName());
        return kafkaTemplate;
    }

}
