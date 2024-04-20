package edu.java.scrapper.configuration;

import edu.java.common.client.BackoffStrategy;
import edu.java.scrapper.configuration.database.AccessType;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotNull @Bean Scheduler scheduler,
    @NotNull ClientConfig stackOverflowClient,
    @NotNull ClientConfig githubClient,
    @NotNull ClientConfig botClient,
    @NotNull AccessType databaseAccessType,
    @NotNull RateLimitConfig rateLimitConfig,
    @NotNull KafkaProducerConfig kafkaProducerConfig,
    boolean useQueue
) {

    public record Scheduler(boolean enable, @NotNull Duration interval, @NotNull Duration forceCheckDelay) {
    }

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

    public record KafkaProducerConfig(
        String bootstrapServers,
        int lingerMs,
        String updatesTopicName,
        String acknowledgments
    ) {
    }

}
