package edu.java.scrapper.client;

import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.dto.StackOverflowAnswersResponse;
import edu.java.scrapper.client.dto.StackOverflowCommentsResponse;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
public class StackOverflowClient {

    private static final String NEW_ANSWERS_FILTER = "!)BlpJaNLs0YCRfKE41h";
    private static final String NEW_COMMENTS_FILTER = "!)BlpJaNLs0YCRfKITiB";
    public static final String SITE_QUERY_PARAM = "site";
    public static final String SITE_QUERY_PARAM_VALUE = "stackoverflow";
    public static final String FILTER_QUERY_PARAM = "filter";
    public static final String FROM_DATE_QUERY_PARAM = "fromdate";

    private final WebClient webClient;
    private final Retry retryPolicy;

    public Activities getNewActivities(long questionId, OffsetDateTime fromDate) {
        if (fromDate == null) {
            throw new IllegalArgumentException("fromDate param cannot be null");
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
            .onStatus(code -> !code.is2xxSuccessful(), this::determineException)
            .toEntity(StackOverflowAnswersResponse.class)
            .retryWhen(retryPolicy);
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
            .onStatus(code -> !code.is2xxSuccessful(), this::determineException)
            .toEntity(StackOverflowCommentsResponse.class)
            .retryWhen(retryPolicy);
    }

    private Mono<Exception> determineException(ClientResponse response) {
        return response
            .bodyToMono(String.class)
            .switchIfEmpty(Mono.just(""))
            .flatMap(error -> Mono.error(new UnsuccessfulRequestException(response.statusCode(), error)));
    }

    public record Activities(StackOverflowAnswersResponse answers, StackOverflowCommentsResponse comments) {
    }

}
