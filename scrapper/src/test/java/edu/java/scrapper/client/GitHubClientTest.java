package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.scrapper.client.dto.GitHubActivityResponse;
import edu.java.scrapper.client.exception.ApiTimeoutException;
import edu.java.common.exception.UnsuccessfulRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@WireMockTest
public class GitHubClientTest {

    static final String TEST_RESPONSE_BODY = """
        [
            {
                "activity_type": "branch_creation",
                "timestamp": "2024-02-08T10:09:48Z"
            },
            {
                "activity_type": "push",
                "timestamp": "2024-02-17T13:09:27Z"
            },
            {
                "activity_type": "push",
                "timestamp": "2024-02-18T08:50:35Z"
            }
        ]
        """;
    static GitHubClient client;

    static OffsetDateTime instantToOffsetDateTime(String date) {
        return Instant.parse(date).atOffset(ZoneOffset.UTC);
    }

    @BeforeAll
    static void createClient(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var webClient = WebClient.builder()
            .baseUrl(wireMockRuntimeInfo.getHttpBaseUrl())
            .build();
        client = new GitHubClient(webClient, new CustomRetrySpecBuilder.Constant());
    }

    @Test
    void newActivitiesShouldBeRequestedCorrectly() {
        stubFor(get("/repos/owner_name/repo_name/activity?time_period=day")
            .willReturn(ok()
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody(TEST_RESPONSE_BODY)));

        var expectedActivities = List.of(
            new GitHubActivityResponse("branch_creation", instantToOffsetDateTime("2024-02-08T10:09:48Z")),
            new GitHubActivityResponse("push", instantToOffsetDateTime("2024-02-17T13:09:27Z")),
            new GitHubActivityResponse("push", instantToOffsetDateTime("2024-02-18T08:50:35Z"))
        );

        var receivedActivities = client.getPastDayActivities("owner_name", "repo_name");

        assertThat(receivedActivities).isEqualTo(expectedActivities);
    }

    @Test
    void badRequestShouldThrow() {
        stubFor(get("/repos/wrong_name/wrong_repo/activity?time_period=day")
            .willReturn(badRequest()
                .withStatus(409)
                .withBody("bad request")));

        assertThatExceptionOfType(UnsuccessfulRequestException.class)
            .isThrownBy(() -> client.getPastDayActivities("wrong_name", "wrong_repo"))
            .extracting(UnsuccessfulRequestException::getStatusCode, UnsuccessfulRequestException::getResponseBody)
            .contains(HttpStatus.CONFLICT, "bad request");
    }

    @Test
    void timeOutResetTimeShouldBeDeterminedCorrectly() {
        stubFor(get("/repos/timeout/timeout/activity?time_period=day")
            .willReturn(badRequest()
                .withStatus(429)
                .withHeader("X-RateLimit-Reset", "1708861220")));

        assertThatExceptionOfType(ApiTimeoutException.class)
            .isThrownBy(() -> client.getPastDayActivities("timeout", "timeout"))
            .extracting(ApiTimeoutException::getRateLimitResetTime)
            .isEqualTo(Instant
                .ofEpochSecond(1708861220)
                .atZone(ZoneId.systemDefault())
                .toOffsetDateTime());
    }

}
