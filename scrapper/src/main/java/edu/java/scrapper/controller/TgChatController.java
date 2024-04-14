package edu.java.scrapper.controller;

import edu.java.scrapper.domain.service.TgChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class TgChatController implements TgChatApi {

    private final TgChatService tgChatService;

    @Override
    public void registerChat(long chatId) {
        log.info("POST /tg-chat/{} endpoint was triggered", chatId);
        tgChatService.register(chatId);
    }

    @Override
    public void deleteChat(long chatId) {
        log.info("DELETE /tg-chat/{} endpoint was triggered", chatId);
        tgChatService.unregister(chatId);
    }

}
