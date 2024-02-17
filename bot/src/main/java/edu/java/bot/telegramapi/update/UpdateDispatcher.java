package edu.java.bot.telegramapi.update;

import com.pengrad.telegrambot.model.BotCommand;
import edu.java.bot.telegramapi.exception.DispatcherException;
import edu.java.bot.telegramapi.response.MarkdownMessage;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class UpdateDispatcher {

    static final MarkdownMessage NOT_A_COMMAND = new MarkdownMessage(
        "Все команды должны начинаться с \"/\"\\. Введите */help*, чтобы просмотреть список всех команд\\.");
    static final MarkdownMessage UNKNOWN_COMMAND = new MarkdownMessage(
        "Неизвестная команда\\. Введите */help*, чтобы просмотреть список всех команд\\.");
    static final MarkdownMessage TRY_AGAIN_LATER = new MarkdownMessage(
        "Ошибка на сервере\\. Повторите ваш запрос позже\\.");
    static final BotCommand HELP_COMMAND = new BotCommand("/help", "список доступных команд");

    private final Object updateProcessor;
    private final Map<String, MethodHandle> commandsMappings = new HashMap<>();
    private final List<BotCommand> botCommands = new ArrayList<>();
    private final MarkdownMessage allCommands = new MarkdownMessage();

    public UpdateDispatcher(Object updateProcessor) {
        this.updateProcessor = updateProcessor;

        var updateProcessorClass = updateProcessor.getClass();
        var lookup = MethodHandles.lookup();

        for (var method : updateProcessorClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(TelegramCommand.class)) {
                validateProcessorMethod(method);

                var annotation = method.getAnnotation(TelegramCommand.class);

                var name = annotation.name();
                var ignoreInHelp = annotation.ignoreInHelp();
                var description = annotation.description();

                if (!ignoreInHelp) {
                    botCommands.add(new BotCommand(name, description));
                }
                try {
                    commandsMappings.put(name, lookup.unreflect(method));
                } catch (IllegalAccessException e) {
                    throw new DispatcherException("unable to register processing method " + method.getName(), e);
                }
            }
        }

        generateAllCommandsMessage();
    }

    public MarkdownMessage processUpdate(long chatId, String message) {
        if (message.isEmpty() || message.charAt(0) != '/') {
            return NOT_A_COMMAND;
        }

        var command = extractCommand(message);
        if (HELP_COMMAND.command().equals(command[0])) {
            return allCommands;
        }
        var processingMethod = commandsMappings.get(command[0]);

        if (processingMethod == null) {
            return UNKNOWN_COMMAND;
        }

        MarkdownMessage result;

        try {
            result = (MarkdownMessage) processingMethod.invoke(updateProcessor, chatId, command[1]);
        } catch (Throwable e) {
            log.error("error when processing update: ", e);
            result = TRY_AGAIN_LATER;
        }

        return result;
    }

    public List<BotCommand> getBotCommands() {
        return Collections.unmodifiableList(botCommands);
    }

    private String[] extractCommand(String message) {
        int firstSpace = message.indexOf(' ');
        if (firstSpace == -1 || firstSpace == message.length() - 1) {
            return new String[] {message, ""};
        }
        return new String[] {
            message.substring(0, firstSpace),
            message.substring(firstSpace + 1).trim()
        };
    }

    private void validateProcessorMethod(Method method) {
        var parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 2
            && parameterTypes[0] == long.class
            && parameterTypes[1] == String.class
            && method.getReturnType() == MarkdownMessage.class) {
            return;
        }
        throw new DispatcherException(
            "update processor must have fixed signature: MarkdownMessage(long, String)");
    }

    private void generateAllCommandsMessage() {
        allCommands.plain("Список доступных команд:");

        botCommands.add(HELP_COMMAND);

        for (var command : botCommands) {
            allCommands.newLine().plain("• ").bold(command.command()).plain(" \\- ").escape(command.description());
        }
    }

}
