package edu.java.scrapper.client;

import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.LinkUpdateRequest;
import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class BotClient {

    public final WebClient webClient;

    public BotClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(
                code -> code.equals(HttpStatus.BAD_REQUEST) || code.equals(HttpStatus.NOT_FOUND),
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new BadBotApiRequestException(response.statusCode().value(), error)))
            )
            .defaultStatusHandler(
                code -> !code.equals(HttpStatus.OK),
                response -> response
                    .bodyToMono(String.class)
                    .flatMap(error -> Mono.error(new UnsuccessfulRequestException(
                        response.statusCode().value(),
                        error
                    )))
            )
            .build();
    }

    public void sendLinkUpdate(long id, String link, String description, List<Long> tgChatIds) {
        if (link == null || description == null || tgChatIds == null) {
            throw new IllegalArgumentException("link, description and thChatIds params cannot be null");
        }
        webClient.post()
            .uri("/updates")
            .bodyValue(new LinkUpdateRequest(id, URI.create(link), description, tgChatIds))
            .retrieve()
            .toBodilessEntity()
            .block();
    }

}
