package edu.java.bot.configuration;

import edu.java.common.client.BackoffStrategy;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull String telegramToken,
    @NotNull ClientConfig scrapperClient,
    @NotNull RateLimitConfig rateLimitConfig,
    @NotNull @Bean KafkaConsumerConfig kafkaConsumerConfig,
    @NotNull KafkaDlqProducerConfig kafkaDlqProducerConfig
) {

    public record ClientConfig(
        String baseUrl,
        int maxRetries,
        Duration retryStep,
        BackoffStrategy backoffStrategy,
        Set<Integer> retryCodes
    ) {
    }

    public record RateLimitConfig(
        Duration cacheExpirationDuration,
        int bucketCapacity,
        Duration refillInterval
    ) {
    }

    public record KafkaConsumerConfig(
        String bootstrapServers,
        String groupId,
        String autoOffsetReset,
        String updatesTopicName,
        List<String> trustedPackages
    ) {
    }

    public record KafkaDlqProducerConfig(
        String bootstrapServers,
        String dlqTopicName,
        String lingerMs
    ) {
    }

}
