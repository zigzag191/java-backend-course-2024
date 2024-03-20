package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public ScrapperClient scrapperClient(WebClient scrapperWebClient) {
        return new ScrapperClient(scrapperWebClient);
    }

    @Bean
    public WebClient scrapperWebClient(WebClient.Builder webClientBuilder, ApplicationConfig config) {
        return webClientBuilder.baseUrl(config.scrapperBaseUrl()).build();
    }

}
