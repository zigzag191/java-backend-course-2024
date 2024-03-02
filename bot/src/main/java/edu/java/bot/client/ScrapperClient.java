package edu.java.bot.client;

import edu.java.bot.client.exception.BadScrapperApiRequestException;
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
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class ScrapperClient {

    public static final String TG_CHAT_TEMPLATE_URL = "/tg-chat/{chatId}";
    public static final String LINKS_TEMPLATE_URL = "/links";
    public static final String TG_CHAT_ID_PARAM_HEADER = "Tg-Chat-Id";
    public static final String NULL_LINK_EXCEPTION_MESSAGE = "link param cannot be null";
    public final WebClient webClient;

    public ScrapperClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(
                code -> code.equals(HttpStatus.BAD_REQUEST) || code.equals(HttpStatus.NOT_FOUND),
                response -> response
                    .bodyToMono(ApiErrorResponse.class)
                    .flatMap(error -> Mono.error(new BadScrapperApiRequestException(
                        response.statusCode().value(),
                        error
                    )))
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

    public void registerChat(long chatId) {
        webClient.post()
            .uri(TG_CHAT_TEMPLATE_URL, chatId)
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    public void deleteChat(long chatId) {
        webClient.delete()
            .uri(TG_CHAT_TEMPLATE_URL, chatId)
            .retrieve()
            .toBodilessEntity()
            .block();
    }

    public ListLinksResponse getAllTrackedLinks(long chatId) {
        return webClient.get()
            .uri(LINKS_TEMPLATE_URL)
            .header(TG_CHAT_ID_PARAM_HEADER, String.valueOf(chatId))
            .retrieve()
            .bodyToMono(ListLinksResponse.class)
            .block();
    }

    public LinkResponse trackLink(long chatId, String link) {
        if (link == null) {
            throw new IllegalArgumentException(NULL_LINK_EXCEPTION_MESSAGE);
        }
        return webClient.post()
            .uri("LINKS_TEMPLATE_URL")
            .bodyValue(new AddLinkRequest(URI.create(link)))
            .header(TG_CHAT_ID_PARAM_HEADER, String.valueOf(chatId))
            .retrieve()
            .bodyToMono(LinkResponse.class)
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
            .bodyToMono(LinkResponse.class)
            .block();
    }

    public SupportedResourcesResponse getSupportedResources() {
        return webClient.get()
            .uri("/links/supported")
            .retrieve()
            .bodyToMono(SupportedResourcesResponse.class)
            .block();
    }

}
