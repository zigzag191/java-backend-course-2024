package edu.java.bot.telegramapi.update;

import edu.java.bot.service.TestUserService;
import edu.java.bot.telegramapi.response.MarkdownMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateProcessorTest {

    TestUserService userService;
    UpdateProcessor updateProcessor;
    UpdateDispatcher updateDispatcher;

    @BeforeEach
    void resetUserService() {
        userService = new TestUserService();
        updateProcessor = new UpdateProcessor(userService);
        updateDispatcher = new UpdateDispatcher(updateProcessor);
    }

    @Test
    void startShouldReturnCorrectResponses() {
        assertThat(updateDispatcher.processUpdate(0, "/start").getText())
            .isEqualTo(UpdateProcessor.SUCCESSFUL_REGISTRATION.getText());
        assertThat(updateDispatcher.processUpdate(0, "/start").getText())
            .isEqualTo(UpdateProcessor.ALREADY_REGISTERED.getText());
    }

    @Test
    void listShouldReturnSpecialMessageIfNoTrackedLinks() {
        updateDispatcher.processUpdate(0, "/start");
        assertThat(updateDispatcher.processUpdate(0, "/list").getText())
            .isEqualTo(UpdateProcessor.NO_TRACKED_LINKS.getText());
    }

    @Test
    void listShouldCorrespondToTrackAndUntrackCommands() {
        updateDispatcher.processUpdate(0, "/start");

        updateDispatcher.processUpdate(0, "/track https://foo.com");
        updateDispatcher.processUpdate(0, "/track https://bar.com");
        updateDispatcher.processUpdate(0, "/track https://baz.com");
        updateDispatcher.processUpdate(0, "/track https://qwerty.com");

        updateDispatcher.processUpdate(0, "/untrack https://bar.com");
        updateDispatcher.processUpdate(0, "/untrack https://qwerty.com");

        var expected = new MarkdownMessage("Отслеживаемые ссылки:").newLine()
            .escape("https://foo.com").newLine()
            .escape("https://baz.com").newLine();

        assertThat(updateDispatcher.processUpdate(0, "/list").getText())
            .isEqualTo(expected.getText());
    }

}
