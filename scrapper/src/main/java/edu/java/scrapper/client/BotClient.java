package edu.java.scrapper.client;

import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.linkupdate.LinkUpdateInfo;
import edu.java.common.dto.linkupdate.LinkUpdateRequest;
import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class BotClient {

    private final WebClient webClient;
    private final Retry retryPolicy;

    public BotClient(WebClient webClient, CustomRetrySpecBuilder builder) {
        this.webClient = webClient;
        this.retryPolicy = builder
            .withStatusCodeFilter(HttpStatusCode::is5xxServerError)
            .build();
    }

    public void sendLinkUpdate(long id, URI link, String description, List<Long> tgChatIds, LinkUpdateInfo info) {
        if (link == null || description == null || tgChatIds == null) {
            throw new IllegalArgumentException("link, description and thChatIds params cannot be null");
        }
        webClient.post()
            .uri("/updates")
            .bodyValue(new LinkUpdateRequest(id, link, description, tgChatIds, info))
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(), this::determineException)
            .toBodilessEntity()
            .retryWhen(retryPolicy)
            .block();
    }

    private Mono<Exception> determineException(ClientResponse response) {
        var status = response.statusCode();
        if (status.equals(HttpStatus.BAD_REQUEST) || status.equals(HttpStatus.NOT_FOUND)) {
            return response
                .bodyToMono(ApiErrorResponse.class)
                .flatMap(error -> Mono.error(new BadBotApiRequestException(
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

}
