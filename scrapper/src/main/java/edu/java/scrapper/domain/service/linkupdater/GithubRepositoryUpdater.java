package edu.java.scrapper.domain.service.linkupdater;

import edu.java.common.dto.linkupdate.GithubRepoUpdateInfo;
import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.GitHubClient;
import edu.java.scrapper.client.dto.GitHubActivityResponse;
import edu.java.scrapper.client.exception.ApiTimeoutException;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.UpdateNotificationService;
import edu.java.scrapper.domain.service.exception.KafkaException;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class GithubRepositoryUpdater implements LinkUpdater {

    private static final URI SUPPORTED_RESOURCE = URI.create("https://github.com/username/repository");
    public static final int REPO_PATH_LENGTH = 3;

    private final GitHubClient gitHubClient;
    private final UpdateNotificationService updateNotificationService;
    private final TgChatService tgChatService;
    private final LinkService linkService;
    private OffsetDateTime apiTimeoutResetTime = OffsetDateTime.now();

    @Override
    public boolean sendUpdates(Link link) {
        if (apiTimeoutResetTime.isAfter(OffsetDateTime.now())) {
            log.warn("skipping update due to github API timeout");
            return false;
        }

        var newActivities = getActivities(link);
        if (newActivities == null || newActivities.isEmpty()) {
            return false;
        }

        return sendUpdate(link, newActivities);
    }

    @Override
    public URI getSupportedResource() {
        return SUPPORTED_RESOURCE;
    }

    private RepoInfo getRepoInfo(URI url) {
        var path = url.getPath().split("/");
        if (path.length != REPO_PATH_LENGTH) {
            throw new UnsupportedResourceException();
        }
        return new RepoInfo(path[1], path[2]);
    }

    private List<GitHubActivityResponse> getActivities(Link link) {
        try {
            var repoInfo = getRepoInfo(link.getUrl());
            return gitHubClient.getPastDayActivities(repoInfo.owner(), repoInfo.repo())
                .stream()
                .filter(activity -> activity.timestamp().isAfter(link.getLastPolled()))
                .toList();
        } catch (UnsuccessfulRequestException ex) {
            log.error("unsuccessful github API request: [{}] {}", ex.getStatusCode(), ex.getResponseBody());
            return null;
        } catch (ApiTimeoutException ex) {
            log.error("github api timeout. resets at: {}", ex.getRateLimitResetTime());
            apiTimeoutResetTime = ex.getRateLimitResetTime();
            return null;
        }
    }

    private boolean sendUpdate(Link link, List<GitHubActivityResponse> activities) {
        try {
            var updateInfo = new GithubRepoUpdateInfo(activities.stream()
                .map(activity -> new GithubRepoUpdateInfo.Activity(
                    mapActivity(activity.activityType()),
                    activity.timestamp()
                ))
                .toList());
            updateNotificationService.send(
                link.getLinkId(),
                link.getUrl(),
                "new repo activity",
                tgChatService.findAllTrackingChats(link).stream()
                    .map(TgChat::getChatId)
                    .toList(),
                updateInfo
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
        } catch (KafkaException ex) {
            log.error("unable to send message to kafka: {}", ex.getMessage());
            return false;
        }
    }

    private GithubRepoUpdateInfo.ActivityType mapActivity(String activity) {
        return GithubRepoUpdateInfo.ActivityType.valueOf(activity.toUpperCase());
    }

    private record RepoInfo(String owner, String repo) {
    }

}
