package edu.java.scrapper;

import edu.java.scrapper.domain.service.LinkUpdaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class LinkUpdateScheduler {

    public final LinkUpdaterService linkUpdaterService;

    @Scheduled(fixedDelayString = "#{@scheduler.interval}")
    public void update() {
        log.info("updating links");
        int updated = linkUpdaterService.updateLinks();
        log.info("done updating links. links updated: {}", updated);
    }

}
