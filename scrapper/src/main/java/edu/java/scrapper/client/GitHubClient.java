package edu.java.scrapper.client;

import edu.java.scrapper.client.dto.GitHubActivityResponse;
import edu.java.scrapper.client.exception.ApiTimeoutException;
import edu.java.scrapper.client.exception.BadRequestException;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class GitHubClient {

    private static final Duration DEFAULT_RATE_LIMIT_TIMEOUT = Duration.ofMinutes(5);

    private final WebClient webClient;

    public GitHubClient(String baseUrl) {
        webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultStatusHandler(code -> !code.is2xxSuccessful(), this::determineException)
            .build();
    }

    public List<GitHubActivityResponse> getPastDayActivities(String owner, String repo) {
        if (owner == null || repo == null) {
            throw new RuntimeException("owner and repo params cannot be null");
        }

        var response = webClient.get()
            .uri(builder -> builder
                .path("/repos/{owner}/{repo}/activities")
                .queryParam("time_period", "day")
                .build(owner, repo))
            .retrieve()
            .toEntity(new ParameterizedTypeReference<List<GitHubActivityResponse>>() {})
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
            return Mono.error(new ApiTimeoutException(timeoutReset));
        }
        if (statusCode.is4xxClientError()) {
            return Mono.error(new BadRequestException(statusCode.value()));
        }
        return Mono.error(new RuntimeException("unable to get result, received status code: " + statusCode.value()));
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
