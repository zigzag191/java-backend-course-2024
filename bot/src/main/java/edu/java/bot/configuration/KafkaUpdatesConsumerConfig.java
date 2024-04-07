package edu.java.bot.configuration;

import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaUpdatesConsumerConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest> kafkaListenerContainerFactory(
        ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory
    ) {
        var factory = new ConcurrentKafkaListenerContainerFactory<Integer, LinkUpdateRequest>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, LinkUpdateRequest> consumerFactory(ApplicationConfig config) {
        return new DefaultKafkaConsumerFactory<>(consumerProps(config.kafkaConsumerConfig()));
    }

    private Map<String, Object> consumerProps(ApplicationConfig.KafkaConsumerConfig config) {
        var props = new HashMap<String, Object>();

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.autoOffsetReset());

        props.put(JsonDeserializer.TRUSTED_PACKAGES, String.join(",", config.trustedPackages()));

        return props;
    }

}
