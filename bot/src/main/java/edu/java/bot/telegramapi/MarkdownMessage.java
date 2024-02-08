package edu.java.bot.telegramapi;

import java.util.Set;

public class MarkdownMessage {

    private static final Set<Character> ESCAPED =
        Set.of('_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!');
    private final StringBuilder builder;

    public MarkdownMessage() {
        this.builder = new StringBuilder();
    }

    public MarkdownMessage(String text) {
        this.builder = new StringBuilder(text);
    }

    public MarkdownMessage escape(String text) {
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (ESCAPED.contains(c)) {
                builder.append("\\");
            }
            builder.append(c);
        }
        return this;
    }

    public MarkdownMessage plain(String text) {
        builder.append(text);
        return this;
    }

    public MarkdownMessage bold(String text) {
        builder.append('*');
        escape(text);
        builder.append('*');
        return this;
    }

    public MarkdownMessage newLine() {
        builder.append('\n');
        return this;
    }

    public String getText() {
        return builder.toString();
    }

}
