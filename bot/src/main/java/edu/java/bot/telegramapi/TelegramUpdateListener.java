package edu.java.bot.telegramapi;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TelegramUpdateListener {

    private final TelegramBot telegramBot;
    private final UpdateDispatcher updateDispatcher;
    private final TelegramMessageSender telegramMessageSender;

    public TelegramUpdateListener(
        TelegramBot telegramBot, UpdateDispatcher updateDispatcher,
        TelegramMessageSender telegramMessageSender
    ) {
        this.telegramBot = telegramBot;
        this.updateDispatcher = updateDispatcher;
        this.telegramMessageSender = telegramMessageSender;
    }

    @PostConstruct
    public void startListeningForUpdates() {
        telegramBot.setUpdatesListener(this::updateListener, log::error);
    }

    private int updateListener(List<Update> updates) {
        updates.forEach(update -> {
            if (update.editedMessage() != null) {
                return;
            }
            long chatId = update.message().chat().id();
            var response = updateDispatcher.processUpdate(update.message().chat().id(), update.message().text());
            var result = telegramMessageSender.send(chatId, response);
            if (!result.isOk()) {
                log.error("error when sending response to " + chatId + ": "
                    + result.description() + " [" + result.errorCode() + "]");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

}
