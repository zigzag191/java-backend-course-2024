package edu.java.bot.telegramapi;

import com.pengrad.telegrambot.TelegramBot;
import edu.java.bot.configuration.ApplicationConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public final class TelegramApiConfiguration {

    private TelegramApiConfiguration() {
    }

    @Bean
    public static UpdateDispatcher updateDispatcher(UpdateProcessor updateProcessor) {
        return new UpdateDispatcher(updateProcessor);
    }

    @Bean
    public static TelegramBot telegramBot(ApplicationConfig config) {
        return new TelegramBot(config.telegramToken());
    }

}
