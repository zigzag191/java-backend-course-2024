package edu.java.scrapper.controller;

import edu.java.common.dto.AddLinkRequest;
import edu.java.common.dto.LinkResponse;
import edu.java.common.dto.ListLinksResponse;
import edu.java.common.dto.RemoveLinkRequest;
import edu.java.common.dto.SupportedResourcesResponse;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.linkupdater.LinkUpdaterManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
public class LinksController implements LinksApi {

    private final LinkService linkService;
    private final LinkUpdaterManager linkUpdaterManager;

    @Override
    public SupportedResourcesResponse getSupportedResources() {
        return new SupportedResourcesResponse(linkUpdaterManager.getSupportedResources());
    }

    @Override
    public ListLinksResponse listLinks(long chatId) {
        log.info("GET /link endpoint was triggered");
        var links = linkService.listAllTrackedLinks(chatId).stream()
            .map(link -> new LinkResponse(link.getLinkId(), link.getUrl()))
            .toList();
        return new ListLinksResponse(links, (long) links.size());
    }

    @Override
    public LinkResponse addLink(long chatId, AddLinkRequest addLinkRequest) {
        log.info("POST /link endpoint was triggered");
        var link = linkService.add(chatId, addLinkRequest.link());
        return new LinkResponse(link.getLinkId(), link.getUrl());
    }

    @Override
    public LinkResponse removeLink(long chatId, RemoveLinkRequest linkRequest) {
        log.info("DELETE /link endpoint was triggered");
        var link = linkService.remove(chatId, linkRequest.link());
        return new LinkResponse(link.getLinkId(), link.getUrl());
    }

}
