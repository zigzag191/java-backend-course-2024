package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import edu.java.common.client.CustomRetrySpecBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfig {

    @Bean
    public ScrapperClient scrapperClient(WebClient scrapperWebClient, ApplicationConfig config) {
        return new ScrapperClient(scrapperWebClient, createRetrySpecBuilder(config.scrapperClient()));
    }

    @Bean
    public WebClient scrapperWebClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        return webClientBuilder.baseUrl(config.scrapperClient().baseUrl()).build();
    }

    private Retry createRetrySpecBuilder(ApplicationConfig.ClientConfig config) {
        var builder = switch (config.backoffStrategy()) {
            case LINEAR -> new CustomRetrySpecBuilder.Linear();
            case EXPONENTIAL -> new CustomRetrySpecBuilder.Exponential();
            case CONSTANT -> new CustomRetrySpecBuilder.Constant();
        };
        return builder
            .withMaxReties(config.maxRetries())
            .withStep(config.retryStep())
            .withStatusCodeFilter(code -> config.retryCodes().contains(code.value()))
            .build();
    }

}
