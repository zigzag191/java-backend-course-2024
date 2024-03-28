package edu.java.scrapper.domain.service.linkupdater;

import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import java.net.URI;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class StackoverflowQuestionUpdater implements LinkUpdater {

    private static final URI SUPPORTED_RESOURCE = URI.create("https://stackoverflow.com/questions/questionId");
    public static final int QUESTION_PATH_LENGTH = 3;

    private final BotClient botClient;
    private final StackOverflowClient stackOverflowClient;
    private final TgChatService tgChatService;
    private final LinkService linkService;

    @Override
    public boolean sendUpdates(Link link) {
        var newActivities = getActivities(link);
        if (newActivities == null
            || (newActivities.answers().items().isEmpty() && newActivities.comments().items().isEmpty())) {
            return false;
        }
        return sendUpdate(link, newActivities);
    }

    @Override
    public URI getSupportedResource() {
        return SUPPORTED_RESOURCE;
    }

    private StackOverflowClient.Activities getActivities(Link link) {
        try {
            long questionId = getQuestionId(link);
            return stackOverflowClient.getNewActivities(questionId, link.getLastPolled());
        } catch (UnsuccessfulRequestException ex) {
            log.error("unsuccessful request to stackoverflow API: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return null;
        }
    }

    private boolean sendUpdate(Link link, StackOverflowClient.Activities activities) {
        try {
            botClient.sendLinkUpdate(
                link.getLinkId(),
                link.getUrl(),
                "new question activity",
                tgChatService.findAllTrackingChats(link).stream()
                    .map(TgChat::getChatId)
                    .toList()
            );
            link.setLastPolled(OffsetDateTime.now());
            linkService.updateLastPolled(link);
            return true;
        } catch (UnsuccessfulRequestException ex) {
            log.error("unsuccessful request to bot API: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return false;
        } catch (BadBotApiRequestException ex) {
            log.error("bad bot API request: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return false;
        }
    }

    private long getQuestionId(Link link) {
        var path = link.getUrl().getPath().split("/");
        if (path.length != QUESTION_PATH_LENGTH) {
            log.error("unable to parse stackoverflow question link {}: invalid path", link.getUrl());
            throw new UnsupportedResourceException();
        }
        try {
            return Long.parseLong(path[2]);
        } catch (NumberFormatException ex) {
            log.error("unable to parse stackoverflow question link {}: invalid questionId", link.getUrl());
            throw new UnsupportedResourceException();
        }
    }

}
