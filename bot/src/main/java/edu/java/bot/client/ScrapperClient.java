package edu.java.bot.client;

import edu.java.bot.client.exception.BadScrapperApiRequestException;
import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.common.dto.AddLinkRequest;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.LinkResponse;
import edu.java.common.dto.ListLinksResponse;
import edu.java.common.dto.RemoveLinkRequest;
import edu.java.common.dto.SupportedResourcesResponse;
import edu.java.common.exception.UnsuccessfulRequestException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class ScrapperClient {

    public static final String TG_CHAT_TEMPLATE_URL = "/tg-chat/{chatId}";
    public static final String LINKS_TEMPLATE_URL = "/links";
    public static final String TG_CHAT_ID_PARAM_HEADER = "Tg-Chat-Id";
    public static final String NULL_LINK_EXCEPTION_MESSAGE = "link param cannot be null";

    private final WebClient webClient;
    private final Retry retryPolicy;

    public ScrapperClient(WebClient webClient, CustomRetrySpecBuilder builder) {
        this.webClient = webClient;
        retryPolicy = builder
            .withStatusCodeFilter(statusCode ->
                statusCode.is5xxServerError() && !statusCode.equals(HttpStatus.NOT_IMPLEMENTED))
            .build();
    }

    public void registerChat(long chatId) {
        webClient.post()
            .uri(TG_CHAT_TEMPLATE_URL, chatId)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .toBodilessEntity()
            .retryWhen(retryPolicy)
            .block();
    }

    public void deleteChat(long chatId) {
        webClient.delete()
            .uri(TG_CHAT_TEMPLATE_URL, chatId)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .toBodilessEntity()
            .retryWhen(retryPolicy)
            .block();
    }

    public ListLinksResponse getAllTrackedLinks(long chatId) {
        return webClient.get()
            .uri(LINKS_TEMPLATE_URL)
            .header(TG_CHAT_ID_PARAM_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .bodyToMono(ListLinksResponse.class)
            .retryWhen(retryPolicy)
            .block();
    }

    public LinkResponse trackLink(long chatId, String link) {
        if (link == null) {
            throw new IllegalArgumentException(NULL_LINK_EXCEPTION_MESSAGE);
        }
        return webClient.post()
            .uri(LINKS_TEMPLATE_URL)
            .bodyValue(new AddLinkRequest(URI.create(link)))
            .header(TG_CHAT_ID_PARAM_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .bodyToMono(LinkResponse.class)
            .retryWhen(retryPolicy)
            .block();
    }

    public LinkResponse untrackLink(long chatId, String link) {
        if (link == null) {
            throw new IllegalArgumentException(NULL_LINK_EXCEPTION_MESSAGE);
        }
        return webClient.method(HttpMethod.DELETE)
            .uri(LINKS_TEMPLATE_URL)
            .bodyValue(new RemoveLinkRequest(URI.create(link)))
            .header(TG_CHAT_ID_PARAM_HEADER, String.valueOf(chatId))
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .bodyToMono(LinkResponse.class)
            .retryWhen(retryPolicy)
            .block();
    }

    public SupportedResourcesResponse getSupportedResources() {
        return webClient.get()
            .uri("/links/supported")
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .bodyToMono(SupportedResourcesResponse.class)
            .retryWhen(retryPolicy)
            .block();
    }

    private Mono<Exception> determineException(ClientResponse response) {
        var status = response.statusCode();
        if (isKnownScrapperError(status)) {
            return response
                .bodyToMono(ApiErrorResponse.class)
                .flatMap(error -> Mono.error(new BadScrapperApiRequestException(
                    status,
                    error
                )));
        }
        return response
            .bodyToMono(String.class)
            .flatMap(error -> Mono.error(new UnsuccessfulRequestException(
                status,
                error
            )));
    }

    private boolean isKnownScrapperError(HttpStatusCode status) {
        return status.equals(HttpStatus.BAD_REQUEST)
            || status.equals(HttpStatus.NOT_FOUND)
            || status.equals(HttpStatus.CONFLICT)
            || status.equals(HttpStatus.NOT_IMPLEMENTED);
    }

}
