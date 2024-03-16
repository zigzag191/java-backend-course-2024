package edu.java.scrapper.domain.service;

import edu.java.scrapper.domain.service.linkprocessor.LinkProcessorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class LinkUpdaterService {

    private final LinkService linkService;
    private final LinkProcessorManager linkProcessorManager;

    public int updateLinks() {
        var links = linkService.findLongestNotPolled(50);
        int updated = 0;
        for (var link : links) {
            boolean linkIsUpdated;
            try {
                linkIsUpdated = linkProcessorManager.sendUpdates(link);
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
