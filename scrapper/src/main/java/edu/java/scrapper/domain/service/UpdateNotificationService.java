package edu.java.scrapper.domain.service;

import edu.java.common.dto.linkupdate.LinkUpdateInfo;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import java.net.URI;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UpdateNotificationService {

    private final BotClient botClient;
    private final ScrapperQueueProducer scrapperQueueProducer;
    private final boolean useQueue;

    public UpdateNotificationService(
        BotClient botClient,
        ScrapperQueueProducer scrapperQueueProducer,
        ApplicationConfig config
    ) {
        this.botClient = botClient;
        this.scrapperQueueProducer = scrapperQueueProducer;
        this.useQueue = config.useQueue();
    }

    public void send(long id, URI link, String description, List<Long> tgChatIds, LinkUpdateInfo info) {
        if (useQueue) {
            scrapperQueueProducer.send(id, link, description, tgChatIds, info);
        } else {
            botClient.sendLinkUpdate(id, link, description, tgChatIds, info);
        }
    }

}
