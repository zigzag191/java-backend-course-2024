package edu.java.bot.telegramapi;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.BaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TelegramUpdateListenerTest {

    @Test
    void updateListenerShouldProcessAllUpdates() {
        var responsesCounter = new AtomicInteger();

        var update = new Update();
        ReflectionTestUtils.setField(update, "message", new Message());
        ReflectionTestUtils.setField(update.message(), "text", "/test");
        ReflectionTestUtils.setField(update.message(), "chat", new Chat());
        ReflectionTestUtils.setField(update.message().chat(), "id", 0L);

        var response = mock(BaseResponse.class);
        when(response.isOk()).thenReturn(true);

        var telegramBot = mock(TelegramBot.class);
        when(telegramBot.execute(any())).thenAnswer(invocation -> {
            responsesCounter.incrementAndGet();
            return response;
        });
        doAnswer(invocation -> {
            var listener = (UpdatesListener) invocation.getArgument(0);
            listener.process(List.of(update, update, update, update));
            listener.process(List.of(update, update));
            listener.process(List.of(update));
            listener.process(List.of());
            return null;
        }).when(telegramBot).setUpdatesListener(any(), (ExceptionHandler) any());

        var updateDispatcher = new UpdateDispatcher(new Object());

        var messageSender = new TelegramMessageSender(telegramBot);
        var updateListener = new TelegramUpdateListener(telegramBot, updateDispatcher, messageSender);

        updateListener.startListeningForUpdates();

        assertThat(responsesCounter).hasValue(7);
    }

}
