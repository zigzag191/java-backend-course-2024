package edu.java.bot.telegramapi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TelegramCommand {
    String name();
    String description() default "no description";
    boolean ignoreInHelp() default false;
}
