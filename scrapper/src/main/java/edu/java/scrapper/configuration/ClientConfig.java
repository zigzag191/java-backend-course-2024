package edu.java.scrapper.configuration;

import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.StackOverflowClient;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    private static final String GITHUB_DEFAULT_BASE_URL = "https://api.github.com";
    private static final String STACKOVERFLOW_DEFAULT_BASE_URL = "https://api.stackexchange.com";

    @Bean
    public StackOverflowClient stackOverflowClient(ApplicationConfig config) {
        var baseUrl = Objects.requireNonNullElse(config.stackOverflowBaseUrl(), STACKOVERFLOW_DEFAULT_BASE_URL);
        return new StackOverflowClient(baseUrl);
    }

    @Bean
    public GitHubClient gitHubClient(ApplicationConfig config) {
        var baseUrl = Objects.requireNonNullElse(config.githubBaseUrl(), GITHUB_DEFAULT_BASE_URL);
        return new GitHubClient(baseUrl);
    }

}
