package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.scrapper.client.dto.StackOverflowAnswersResponse;
import edu.java.scrapper.client.dto.StackOverflowCommentsResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import edu.java.common.exception.UnsuccessfulRequestException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@WireMockTest
public class StackOverflowClientTest {

    static OffsetDateTime zeroTimeStamp =
        OffsetDateTime.of(LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC), ZoneOffset.UTC);
    static StackOverflowClient client;

    @BeforeAll
    static void createClient(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var webClient = WebClient.builder()
            .baseUrl(wireMockRuntimeInfo.getHttpBaseUrl())
            .build();
        client = new StackOverflowClient(webClient, new CustomRetrySpecBuilder.Linear()
            .withMaxReties(3)
            .withStep(Duration.ofSeconds(1))
            .build());
    }

    @Test
    void newActivitiesShouldBeRequestedCorrectly() {
        stubFor(get("/questions/1/answers?site=stackoverflow&filter=!)BlpJaNLs0YCRfKE41h&fromdate=0")
            .willReturn(ok()
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody("{\"items\":[{\"link\":\"https://example1.com\"}]}")));

        stubFor(get("/questions/1/comments?site=stackoverflow&filter=!)BlpJaNLs0YCRfKITiB&fromdate=0")
            .willReturn(ok()
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody("{\"items\":[{\"link\":\"https://example2.com\"}]}")));

        var expected = new StackOverflowClient.Activities(
            new StackOverflowAnswersResponse(List.of(new StackOverflowAnswersResponse.Answer("https://example1.com"))),
            new StackOverflowCommentsResponse(List.of(new StackOverflowCommentsResponse.Comment("https://example2.com")))
        );

        var comments = client.getNewActivities(1, zeroTimeStamp);

        assertThat(comments).isEqualTo(expected);
    }

    @Test
    void serverErrorShouldThrow() {
        stubFor(get("/questions/123/answers?site=stackoverflow&filter=!)BlpJaNLs0YCRfKE41h&fromdate=0")
            .willReturn(serverError()
                .withStatus(500)
                .withBody("server error")));
        stubFor(get("/questions/123/comments?site=stackoverflow&filter=!)BlpJaNLs0YCRfKITiB&fromdate=0")
            .willReturn(ok()
                .withHeader("Content-Type", "application/json; charset=utf-8")
                .withBody("{\"items\":[{\"link\":\"https://example2.com\"}]}")));

        assertThatExceptionOfType(UnsuccessfulRequestException.class)
            .isThrownBy(() -> client.getNewActivities(123, zeroTimeStamp))
            .extracting(UnsuccessfulRequestException::getStatusCode, UnsuccessfulRequestException::getResponseBody)
            .contains(HttpStatus.INTERNAL_SERVER_ERROR, "server error");
    }

}
