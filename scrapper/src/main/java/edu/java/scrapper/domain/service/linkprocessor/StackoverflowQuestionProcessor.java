package edu.java.scrapper.domain.service.linkprocessor;

import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.StackOverflowClient;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
public class StackoverflowQuestionProcessor implements LinkProcessor {

    private static final URI SUPPORTED_RESOURCE = URI.create("https://stackoverflow.com/questions/questionId");

    private final BotClient botClient;
    private final StackOverflowClient stackOverflowClient;
    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcLinkRepository linkRepository;

    @Override
    public Link createLink(URI url) {
        var path = url.getPath().split("/");
        if (url.getHost().equals("stackoverflow.com")
            && path.length == 3
            && path[1].equals("questions") && isNumber(path[2])) {
            return new Link(url, LinkType.STACK_OVERFLOW_QUESTION, OffsetDateTime.now());
        }
        return null;
    }

    @Override
    public boolean sendUpdates(Link link) {
        long questionId = getQuestionId(link);
        StackOverflowClient.Activities newActivities;
        try {
            newActivities = stackOverflowClient.getNewActivities(questionId, link.getLastPolled());
        } catch (UnsuccessfulRequestException ex) {
            log.error("unsuccessful request to stackoverflow API: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return false;
        }
        if (!newActivities.answers().items().isEmpty() || !newActivities.comments().items().isEmpty()) {
            try {
                botClient.sendLinkUpdate(
                    link.getLinkId(),
                    link.getUrl(),
                    "new question activity",
                    tgChatRepository.findAllTrackingChats(link).stream()
                        .map(TgChat::getChatId)
                        .toList()
                );
                link.setLastPolled(OffsetDateTime.now());
                linkRepository.updateLastPolled(link);
            } catch (UnsuccessfulRequestException ex) {
                log.error("unsuccessful request to bot API: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
                return false;
            } catch (BadBotApiRequestException ex) {
                log.error("bad bot API request: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
                return false;
            }
        }
        return true;
    }

    @Override
    public URI getSupportedResource() {
        return SUPPORTED_RESOURCE;
    }

    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private long getQuestionId(Link link) {
        var path = link.getUrl().getPath().split("/");
        if (path.length != 3) {
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
