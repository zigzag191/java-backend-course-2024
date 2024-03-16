package edu.java.scrapper.domain.service.linkprocessor;

import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.BotClient;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.dto.GitHubActivityResponse;
import edu.java.scrapper.client.exception.ApiTimeoutException;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class GithubRepositoryProcessor implements LinkProcessor {

    private static final URI SUPPORTED_RESOURCE = URI.create("https://github.com/username/repository");

    private final GitHubClient gitHubClient;
    private final BotClient botClient;
    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcLinkRepository linkRepository;
    private OffsetDateTime apiTimeoutResetTime = OffsetDateTime.now();

    @Override
    public Link createLink(URI url) {
        var path = url.getPath().split("/");
        if (url.getHost().equals("github.com") && path.length == 3) {
            return new Link(url, LinkType.GITHUB_REPOSITORY, OffsetDateTime.now());
        }
        return null;
    }

    @Override
    public boolean sendUpdates(Link link) {
        if (apiTimeoutResetTime.isAfter(OffsetDateTime.now())) {
            log.warn("skipping update due to github API timeout");
            return false;
        }

        var repoInfo = getRepoInfo(link.getUrl());
        List<GitHubActivityResponse> activities;
        try {
            activities = gitHubClient.getPastDayActivities(repoInfo.owner(), repoInfo.repo());
        } catch (UnsuccessfulRequestException ex) {
            log.error("unsuccessful github API request: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return false;
        } catch (ApiTimeoutException ex) {
            log.error("github api timeout. resets at: {}", ex.getRateLimitResetTime());
            apiTimeoutResetTime = ex.getRateLimitResetTime();
            return false;
        }

        var newActivities = activities.stream()
            .filter(activity -> activity.timestamp().isAfter(link.getLastPolled()))
            .toList();

        if (!newActivities.isEmpty()) {
            try {
                botClient.sendLinkUpdate(
                    link.getLinkId(),
                    link.getUrl(),
                    "new repo activity",
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

    private RepoInfo getRepoInfo(URI url) {
        var path = url.getPath().split("/");
        if (path.length != 3) {
            throw new UnsupportedResourceException();
        }
        return new RepoInfo(path[1], path[2]);
    }

    private record RepoInfo(String owner, String repo) {}

}
