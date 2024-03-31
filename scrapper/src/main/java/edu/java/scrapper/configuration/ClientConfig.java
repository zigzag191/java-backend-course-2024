package edu.java.scrapper.configuration;

import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    private static final String GITHUB_DEFAULT_BASE_URL = "https://api.github.com";
    private static final String STACKOVERFLOW_DEFAULT_BASE_URL = "https://api.stackexchange.com";

    @Bean
    public StackOverflowClient stackOverflowClient(WebClient stackOverflowWebClient, ApplicationConfig config) {
        return new StackOverflowClient(stackOverflowWebClient, createRetrySpecBuilder(config.stackOverflowClient()));
    }

    @Bean
    public GitHubClient gitHubClient(WebClient gitHubWebClient, ApplicationConfig config) {
        return new GitHubClient(gitHubWebClient, createRetrySpecBuilder(config.githubClient()));
    }

    @Bean
    public WebClient gitHubWebClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        var baseUrl = Objects.requireNonNullElse(config.githubClient().baseUrl(), GITHUB_DEFAULT_BASE_URL);
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    @Bean
    public WebClient stackOverflowWebClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        var baseUrl = Objects.requireNonNullElse(
            config.stackOverflowClient().baseUrl(),
            STACKOVERFLOW_DEFAULT_BASE_URL
        );
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    @Bean
    public BotClient botClient(WebClient botWebClient, ApplicationConfig config) {
        return new BotClient(botWebClient, createRetrySpecBuilder(config.botClient()));
    }

    @Bean
    public WebClient botWebClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        return webClientBuilder.baseUrl(config.botClient().baseUrl()).build();
    }

    private CustomRetrySpecBuilder createRetrySpecBuilder(ApplicationConfig.ClientConfig config) {
        var builder = switch (config.backoffStrategy()) {
            case LINEAR -> new CustomRetrySpecBuilder.Linear();
            case EXPONENTIAL -> new CustomRetrySpecBuilder.Exponential();
            case CONSTANT -> new CustomRetrySpecBuilder.Constant();
        };
        return builder
            .withMaxReties(config.maxRetries())
            .withStep(config.retryStep());
    }

}
