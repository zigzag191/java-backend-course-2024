package edu.java.bot.telegramapi;

import edu.java.bot.service.UserService;
import edu.java.bot.telegramapi.exceptions.InvalidUrlException;
import edu.java.bot.telegramapi.exceptions.UnsupportedResourceException;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("MultipleStringLiterals")
public class UpdateProcessor {

    static final MarkdownMessage SUCCESSFUL_REGISTRATION = new MarkdownMessage(
        "Вы были успешно зарегистрированы\\. Введите */help*, чтобы посмотреть список доступных команд");
    static final MarkdownMessage ALREADY_REGISTERED = new MarkdownMessage(
        "Вы уже зарегистрированы\\.");
    static final MarkdownMessage NO_TRACKED_LINKS = new MarkdownMessage(
        "Список пуст\\. Чтобы добавить ссылки для отслеживания, используйте команду */track*\\.");

    private final UserService userService;

    public UpdateProcessor(UserService userService) {
        this.userService = userService;
    }

    @TelegramCommand(name = "/start", ignoreInHelp = true)
    public MarkdownMessage startCommand(long chatId, String text) {
        if (userService.registerUser(chatId)) {
            return SUCCESSFUL_REGISTRATION;
        } else {
            return ALREADY_REGISTERED;
        }
    }

    @TelegramCommand(name = "/track", description = "начать отслеживание ссылки")
    public MarkdownMessage trackCommand(long chatId, String text) {
        try {
            if (userService.trackLink(chatId, text)) {
                return new MarkdownMessage("Ссылка ").escape(text).plain(" была добавлена\\.");
            } else {
                return new MarkdownMessage("Ссылка ").escape(text).plain(" уже отслеживается\\.");
            }
        } catch (InvalidUrlException e) {
            return new MarkdownMessage("Ссылка ").escape(text).plain(" не является корректным URL\\.");
        } catch (UnsupportedResourceException e) {
            return new MarkdownMessage("Ресурс по ссылке ").escape(text).plain(" не поддерживается");
        }
    }

    @TelegramCommand(name = "/untrack", description = "прекратить отслеживание ссылки")
    public MarkdownMessage untrackCommand(long chatId, String text) {
        if (userService.untrackLink(chatId, text)) {
            return new MarkdownMessage("Ссылка ").escape(text).plain(" больше не отслеживается\\.");
        } else {
            return new MarkdownMessage("Ссылка ").escape(text).plain(" не отслеживалась\\.");
        }
    }

    @TelegramCommand(name = "/list", description = "показать список отслеживаемых ссылок")
    public MarkdownMessage listCommand(long chatId, String text) {
        var response = new MarkdownMessage();
        var links = userService.getTrackedLinks(chatId);
        if (links.isEmpty()) {
            return NO_TRACKED_LINKS;
        }
        response.plain("Отслеживаемые ссылки:").newLine();
        for (var link : links) {
            response.escape(link).newLine();
        }
        return response;
    }

}
