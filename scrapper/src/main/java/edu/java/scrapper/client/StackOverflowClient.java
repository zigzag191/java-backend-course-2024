package edu.java.scrapper.client;

import edu.java.scrapper.client.dto.StackOverflowAnswersResponse;
import edu.java.scrapper.client.dto.StackOverflowCommentsResponse;
import edu.java.scrapper.client.exception.BadRequestException;
import java.time.OffsetDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class StackOverflowClient {

    private static final String NEW_ANSWERS_FILTER = "!)BlpJaNLs0YCRfKE41h";
    private static final String NEW_COMMENTS_FILTER = "!)BlpJaNLs0YCRfKITiB";
    public static final String SITE_QUERY_PARAM = "site";
    public static final String SITE_QUERY_PARAM_VALUE = "stackoverflow";
    public static final String FILTER_QUERY_PARAM = "filter";
    public static final String FROM_DATE_QUERY_PARAM = "fromdate";

    private final WebClient webClient;

    public StackOverflowClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(code -> !code.is2xxSuccessful(), this::determineException)
            .build();
    }

    public Activities getNewActivities(long questionId, OffsetDateTime fromDate) {
        if (fromDate == null) {
            throw new RuntimeException("fromDate param cannot be null");
        }

        var newAnswers = getNewAnswers(questionId, fromDate);
        var newComments = getNewComments(questionId, fromDate);

        var responses = Mono.zip(newAnswers, newComments).block();

        if (responses == null) {
            throw new RuntimeException("unable to receive response");
        }

        return new Activities(responses.getT1().getBody(), responses.getT2().getBody());
    }

    private Mono<ResponseEntity<StackOverflowAnswersResponse>> getNewAnswers(
        long questionId,
        OffsetDateTime fromDate
    ) {
        return webClient.get()
            .uri(builder -> builder
                .path("/questions/{questionId}/answers")
                .queryParam(SITE_QUERY_PARAM, SITE_QUERY_PARAM_VALUE)
                .queryParam(FILTER_QUERY_PARAM, NEW_ANSWERS_FILTER)
                .queryParam(FROM_DATE_QUERY_PARAM, fromDate.toEpochSecond())
                .build(questionId))
            .retrieve()
            .toEntity(StackOverflowAnswersResponse.class);
    }

    private Mono<ResponseEntity<StackOverflowCommentsResponse>> getNewComments(
        long questionId,
        OffsetDateTime fromDate
    ) {
        return webClient.get()
            .uri(builder -> builder
                .path("/questions/{questionId}/comments")
                .queryParam(SITE_QUERY_PARAM, SITE_QUERY_PARAM_VALUE)
                .queryParam(FILTER_QUERY_PARAM, NEW_COMMENTS_FILTER)
                .queryParam(FROM_DATE_QUERY_PARAM, fromDate.toEpochSecond())
                .build(questionId))
            .retrieve()
            .toEntity(StackOverflowCommentsResponse.class);
    }

    private Mono<Exception> determineException(ClientResponse response) {
        var statusCode = response.statusCode();
        if (statusCode.is4xxClientError()) {
            return Mono.error(new BadRequestException(statusCode.value()));
        }
        return Mono.error(new RuntimeException("unable to get result, received status code: " + statusCode.value()));
    }

    public record Activities(StackOverflowAnswersResponse answers, StackOverflowCommentsResponse comments) {
    }

}
