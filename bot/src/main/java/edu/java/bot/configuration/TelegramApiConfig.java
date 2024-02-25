package edu.java.bot.configuration;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import edu.java.bot.telegramapi.update.UpdateDispatcher;
import edu.java.bot.telegramapi.update.UpdateProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramApiConfig {

    @Bean
    public UpdateDispatcher updateDispatcher(UpdateProcessor updateProcessor, TelegramBot telegramBot) {
        var updateDispatcher = new UpdateDispatcher(updateProcessor);
        var commands = new SetMyCommands(updateDispatcher.getBotCommands().toArray(BotCommand[]::new));
        telegramBot.execute(commands);
        return updateDispatcher;
    }

    @Bean
    public TelegramBot telegramBot(ApplicationConfig config) {
        return new TelegramBot(config.telegramToken());
    }

}
