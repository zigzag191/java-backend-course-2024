package edu.java.bot.configuration;

import edu.java.common.client.BackoffStrategy;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
    @NotEmpty String telegramToken,
    @NotEmpty ClientConfig scrapperClient
) {

    public record ClientConfig(
        String baseUrl,
        int maxRetries,
        Duration retryStep,
        BackoffStrategy backoffStrategy
    ) {
    }

}
