package edu.java.scrapper.client;

import edu.java.common.exception.UnsuccessfulRequestException;
import edu.java.scrapper.client.dto.GitHubActivityResponse;
import edu.java.scrapper.client.exception.ApiTimeoutException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@RequiredArgsConstructor
public class GitHubClient {

    private static final Duration DEFAULT_RATE_LIMIT_TIMEOUT = Duration.ofMinutes(5);

    private final WebClient webClient;
    private final Retry retryPolicy;

    public List<GitHubActivityResponse> getPastDayActivities(String owner, String repo) {
        if (owner == null || repo == null) {
            throw new IllegalArgumentException("owner and repo params cannot be null");
        }

        var response = webClient.get()
            .uri(builder -> builder
                .path("/repos/{owner}/{repo}/activity")
                .queryParam("time_period", "day")
                .build(owner, repo))
            .retrieve()
            .onStatus(code -> !code.is2xxSuccessful(), this::determineException)
            .toEntity(new ParameterizedTypeReference<List<GitHubActivityResponse>>() {})
            .retryWhen(retryPolicy)
            .block();

        if (response == null) {
            throw new RuntimeException("unable to receive response");
        }

        return response.getBody();
    }

    private Mono<Exception> determineException(ClientResponse response) {
        var statusCode = response.statusCode();
        if (isRateLimitTimeOut(statusCode)) {
            var timeoutReset = getRateLimitTimeOutResetTime(response);
            return Mono.error(new ApiTimeoutException(statusCode, timeoutReset));
        }
        return response
            .bodyToMono(String.class)
            .switchIfEmpty(Mono.just(""))
            .flatMap(error -> Mono.error(new UnsuccessfulRequestException(statusCode, error)));
    }

    private boolean isRateLimitTimeOut(HttpStatusCode statusCode) {
        return statusCode.value() == HttpStatus.FORBIDDEN.value()
            || statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value();
    }

    private OffsetDateTime getRateLimitTimeOutResetTime(ClientResponse response) {
        var rateLimitReset = response.headers().header("x-ratelimit-reset");
        if (!rateLimitReset.isEmpty()) {
            long timestamp = Long.parseLong(rateLimitReset.getFirst());
            return OffsetDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.systemDefault());
        } else {
            return OffsetDateTime.now().plus(DEFAULT_RATE_LIMIT_TIMEOUT);
        }
    }

}
