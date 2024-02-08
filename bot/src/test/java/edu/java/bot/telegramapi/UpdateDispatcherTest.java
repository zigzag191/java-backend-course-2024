package edu.java.bot.telegramapi;

import edu.java.bot.telegramapi.exceptions.DispatcherException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class UpdateDispatcherTest {

    public static Stream<Arguments> updateDispatcherShouldPassParamsCorrectly() {
        return Stream.of(
            arguments("/command qwerty", "qwerty"),
            arguments("/command    q   aba   cab       ", "q   aba   cab"),
            arguments("/command", ""),
            arguments("/command         ", "")
        );
    }

    public static Stream<Object> updateDispatcherShouldNotAllowIncorrectProcessors() {
        return Stream.of(
            new Object() {@TelegramCommand(name = "command") public void command(long chatId, String text) {}},
            new Object() {
                @TelegramCommand(name = "command")
                public MarkdownMessage command() {
                    return new MarkdownMessage();
                }
            },
            new Object() {
                @TelegramCommand(name = "command")
                public MarkdownMessage command(String text, long chatID) {
                    return new MarkdownMessage();
                }
            },
            new Object() {
                @TelegramCommand(name = "command")
                public MarkdownMessage command(long chatId, String text, int b) {
                    return new MarkdownMessage();
                }
            }
        );
    }

    @ParameterizedTest
    @MethodSource
    void updateDispatcherShouldNotAllowIncorrectProcessors(Object incorrectUpdateProcessor) {
        assertThatExceptionOfType(DispatcherException.class)
            .isThrownBy(() -> new UpdateDispatcher(incorrectUpdateProcessor));
    }

    @ParameterizedTest
    @MethodSource
    void updateDispatcherShouldPassParamsCorrectly(String initialCommand, String expectedParameter) {
        var updateProcessor = new Object() {
            @TelegramCommand(name = "/command")
            public MarkdownMessage command(long chatId, String text) {
                assertThat(text).isEqualTo(expectedParameter);
                return new MarkdownMessage();
            }
        };

        var updateDispatcher = new UpdateDispatcher(updateProcessor);
        updateDispatcher.processUpdate(0, initialCommand);
    }

    @Test
    void updateDispatcherShouldDispatchCorrectly() {
        var updateProcessor = new Object() {
            @TelegramCommand(name = "/command1")
            public MarkdownMessage command1(long chatId, String text) {
                return new MarkdownMessage("result1");
            }
            @TelegramCommand(name = "/command2")
            public MarkdownMessage command2(long chatId, String text) {
                return new MarkdownMessage("result2");
            }
            @TelegramCommand(name = "/command3")
            public MarkdownMessage command3(long chatId, String text) {
                return new MarkdownMessage("result3");
            }
        };

        var updateDispatcher = new UpdateDispatcher(updateProcessor);

        assertThat(updateDispatcher.processUpdate(0, "/command1 qwerty").getText()).isEqualTo("result1");
        assertThat(updateDispatcher.processUpdate(0, "/command2").getText()).isEqualTo("result2");
        assertThat(updateDispatcher.processUpdate(0, "/command3 1").getText()).isEqualTo("result3");
    }

    @Test
    void updateDispatcherShouldReturnRightResponsesIfRequestIsIncorrect() {
        var updateProcessor = new Object();
        var updateDispatcher = new UpdateDispatcher(updateProcessor);

        assertThat(updateDispatcher.processUpdate(0, "not a command").getText())
            .isEqualTo(UpdateDispatcher.NOT_A_COMMAND.getText());

        assertThat(updateDispatcher.processUpdate(0, "/unknown command").getText())
            .isEqualTo(UpdateDispatcher.UNKNOWN_COMMAND.getText());
    }

}
