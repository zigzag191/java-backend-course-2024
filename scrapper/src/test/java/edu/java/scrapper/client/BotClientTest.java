package edu.java.scrapper.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.common.dto.ApiErrorResponse;
import edu.java.scrapper.client.exception.BadBotApiRequestException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
        client = new BotClient(wireMockRuntimeInfo.getHttpBaseUrl());
    }

    @Test
    void requestBodyShouldBeCreatedCorrectly() {
        stubFor(post("/updates")
            .withRequestBody(containing("\"id\":1"))
            .withRequestBody(containing("\"url\":\"http://example.com\""))
            .withRequestBody(containing("\"description\":\"test\""))
            .withRequestBody(containing("\"tgChatIds\":[1,2,3]}"))
            .willReturn(ok()));

        assertThatNoException().isThrownBy(() -> client.sendLinkUpdate(
            1,
            "http://example.com",
            "test",
            List.of(1L, 2L, 3L)
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
            .isThrownBy(() -> client.sendLinkUpdate(0, "", "", List.of()))
            .extracting(BadBotApiRequestException::getResponseBody)
            .isEqualTo(expectedResponse);
    }

}
