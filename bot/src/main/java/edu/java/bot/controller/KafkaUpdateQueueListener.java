package edu.java.bot.controller;

import edu.java.bot.service.UserService;
import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class KafkaUpdateQueueListener {

    private final UserService userService;

    @KafkaListener(topics = "#{@kafkaConsumerConfig.updatesTopicName}", errorHandler = "dlqExceptionHandler")
    public void listen(LinkUpdateRequest update) {
        log.info("Updating links: {}", update);
        userService.sendUpdates(update.url(), update.tgChatIds(), update.linkUpdateInfo());
    }

}
