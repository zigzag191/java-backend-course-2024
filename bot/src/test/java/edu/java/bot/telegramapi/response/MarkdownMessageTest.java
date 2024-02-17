package edu.java.bot.telegramapi.response;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class MarkdownMessageTest {

    public static Stream<Arguments> appendedTextShouldBeEscapedCorrectly() {
        return Stream.of(
            arguments("qwe-rty", "qwe\\-rty"),
            arguments("_*[]()~`>#+-=|{}.!", "\\_\\*\\[\\]\\(\\)\\~\\`\\>\\#\\+\\-\\=\\|\\{\\}\\.\\!"),
            arguments("!Hello. World!", "\\!Hello\\. World\\!")
        );
    }

    @ParameterizedTest
    @MethodSource
    void appendedTextShouldBeEscapedCorrectly(String text, String expected) {
        var message = new MarkdownMessage().escape(text);
        assertThat(message.getText()).isEqualTo(expected);
    }

}
