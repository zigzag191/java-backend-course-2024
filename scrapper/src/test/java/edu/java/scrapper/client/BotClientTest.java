package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.common.client.CustomRetrySpecBuilder;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.common.dto.linkupdate.StackoverflowQuestionUpdateInfo;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

@WireMockTest
public class BotClientTest {

    static BotClient client;

    static final String NOT_FOUND_RESPONSE = """
        {
            "description": "test-description",
            "code": 404,
            "exceptionName": "test-name",
            "exceptionMessage": "test-message",
            "stackTrace": [
                "a",
                "b",
                "c"
            ]
        }
        """;

    @BeforeAll
    static void createClient(WireMockRuntimeInfo wireMockRuntimeInfo) {
        var webClient = WebClient.builder().baseUrl(wireMockRuntimeInfo.getHttpBaseUrl()).build();
        client = new BotClient(webClient, new CustomRetrySpecBuilder.Exponential()
            .withMaxReties(3)
            .withStep(Duration.ofSeconds(1))
            .build());
    }

    @Test
    void requestBodyShouldBeCreatedCorrectly() {
        stubFor(post("/updates")
            .withRequestBody(containing("\"id\":1"))
            .withRequestBody(containing("\"url\":\"http://example.com\""))
            .withRequestBody(containing("\"description\":\"test\""))
            .withRequestBody(containing("\"tgChatIds\":[1,2,3]"))
            .willReturn(ok()));

        assertThatNoException().isThrownBy(() -> client.sendLinkUpdate(
            1,
            URI.create("http://example.com"),
            "test",
            List.of(1L, 2L, 3L),
            new StackoverflowQuestionUpdateInfo(List.of(), List.of())
        ));
    }

    @Test
    void errorResponseShouldBeReceivedCorrectly() {
        stubFor(post("/updates")
            .willReturn(notFound()
                .withHeader("Content-Type", "application/json")
                .withBody(NOT_FOUND_RESPONSE)));

        var expectedResponse =
            new ApiErrorResponse("test-description", "404", "test-name", "test-message", List.of("a", "b", "c"));

        assertThatExceptionOfType(BadBotApiRequestException.class)
            .isThrownBy(() -> client.sendLinkUpdate(0, URI.create(""), "", List.of(), null))
            .extracting(BadBotApiRequestException::getResponseBody)
            .isEqualTo(expectedResponse);
    }

}
