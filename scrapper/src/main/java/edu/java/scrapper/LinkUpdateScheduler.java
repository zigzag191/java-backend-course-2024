package edu.java.scrapper;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.linkupdater.LinkUpdaterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkUpdateScheduler {

    public static final int BATCH_SIZE = 50;
    private final LinkService linkService;
    private final LinkUpdaterManager linkUpdaterManager;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        log.info("updating links");
        int updated = updateLinks();
        log.info("done updating links. links updated: {}", updated);
    }

    private int updateLinks() {
        var links = linkService.findLongestNotPolled(BATCH_SIZE);
        int updated = 0;
        for (var link : links) {
            boolean linkIsUpdated;
            try {
                linkIsUpdated = linkUpdaterManager.sendUpdates(link);
            } catch (Exception ex) {
                linkIsUpdated = false;
            }
            if (!linkIsUpdated) {
                log.warn("link was not updated: {}", link.getUrl());
            }
        }
        return updated;
    }

}
