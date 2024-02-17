package edu.java.bot.telegramapi.response;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import org.springframework.stereotype.Component;

@Component
public class TelegramMessageSender {

    private final TelegramBot telegramBot;

    public TelegramMessageSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public BaseResponse send(long chatId, MarkdownMessage message) {
        var sendMessage = new SendMessage(chatId, message.getText())
            .parseMode(ParseMode.MarkdownV2);
        return telegramBot.execute(sendMessage);
    }

}
