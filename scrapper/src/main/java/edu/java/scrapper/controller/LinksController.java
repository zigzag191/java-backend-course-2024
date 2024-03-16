package edu.java.scrapper.controller;

import edu.java.common.dto.AddLinkRequest;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.LinkResponse;
import edu.java.common.dto.ListLinksResponse;
import edu.java.common.dto.RemoveLinkRequest;
import edu.java.common.dto.SupportedResourcesResponse;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.linkprocessor.LinkProcessorManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@Log4j2
@ApiResponses({
    @ApiResponse(responseCode = "400",
                 description = "Некорректные параметры запроса",
                 content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
@RequiredArgsConstructor
public class LinksController {

    private final LinkService linkService;
    private final LinkProcessorManager linkProcessorManager;

    @Operation(summary = "Получить список доступных для отслеживания ресурсов")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "Список успешно получен"))
    @GetMapping("/supported")
    public SupportedResourcesResponse getSupportedResources() {
        return new SupportedResourcesResponse(linkProcessorManager.getSupportedResources());
    }

    @Operation(summary = "Получить все отслеживаемые ссылки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ссылки успешно получены"),
        @ApiResponse(responseCode = "404",
                     description = "Чат не существует",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
    @GetMapping
    public ListLinksResponse listLinks(@RequestHeader("Tg-Chat-Id") long chatId) {
        log.info("GET /link endpoint was triggered");
        var links = linkService.listAllTrackedLinks(chatId).stream()
            .map(link -> new LinkResponse(link.getLinkId(), link.getUrl()))
            .toList();
        return new ListLinksResponse(links, (long) links.size());
    }

    @Operation(summary = "Добавить отслеживание ссылки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ссылка успешно добавлена"),
        @ApiResponse(responseCode = "409",
                     description = "Ссылка уже отслеживается",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "501",
                     description = "Ресурс по данной ссылке не поддерживается",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
    @PostMapping
    public LinkResponse addLink(@RequestHeader("Tg-Chat-Id") long chatId, @RequestBody AddLinkRequest addLinkRequest) {
        log.info("POST /link endpoint was triggered");
        var link = linkService.add(chatId, addLinkRequest.link());
        return new LinkResponse(link.getLinkId(), link.getUrl());
    }

    @Operation(summary = "Убрать отслеживание ссылки")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ссылка успешно убрана"),
        @ApiResponse(responseCode = "404",
                     description = "Ссылка не найдена",
                     content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))})
    @DeleteMapping
    public LinkResponse removeLink(@RequestHeader("Tg-Chat-Id") long chatId, @RequestBody RemoveLinkRequest linkRequest) {
        log.info("DELETE /link endpoint was triggered");
        var link = linkService.remove(chatId, linkRequest.link());
        return new LinkResponse(link.getLinkId(), link.getUrl());
    }

}
