package edu.java.bot.telegramapi;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class CommandMenuSetter {

    private final TelegramBot telegramBot;
    private final UpdateDispatcher updateDispatcher;

    public CommandMenuSetter(TelegramBot telegramBot, UpdateDispatcher updateDispatcher) {
        this.telegramBot = telegramBot;
        this.updateDispatcher = updateDispatcher;
    }

    @PostConstruct
    public void setCommandMenu() {
        var commands = new SetMyCommands(updateDispatcher.getBotCommands().toArray(BotCommand[]::new));
        telegramBot.execute(commands);
    }

}
