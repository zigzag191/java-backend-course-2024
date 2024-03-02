package edu.java.bot.configuration;

import edu.java.bot.client.ScrapperClient;
import org.springframework.context.annotation.Bean;

public class ClientConfig {

    @Bean
    public ScrapperClient scrapperClient(ApplicationConfig config) {
        return new ScrapperClient(config.scrapperBaseUrl());
    }

}
