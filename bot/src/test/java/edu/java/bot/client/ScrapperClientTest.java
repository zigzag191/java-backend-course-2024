package edu.java.bot.client;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import edu.java.common.dto.LinkResponse;
import edu.java.common.dto.ListLinksResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@WireMockTest
public class ScrapperClientTest {

    static final String CONTENT_TYPE_HEADER = "Content-Type";
    static final String CONTENT_TYPE_VALUE = "application/json";
    static ScrapperClient client;

    @BeforeAll
    static void initClient(WireMockRuntimeInfo wireMockRuntimeInfo) {
        client = new ScrapperClient(wireMockRuntimeInfo.getHttpBaseUrl());
    }

    @Test
    void registerChatCommandShouldWorkCorrectly() {
        stubFor(post("/tg-chat/1").willReturn(ok()
            .withHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)));
        assertThatNoException().isThrownBy(() -> client.registerChat(1));
    }

    @Test
    void deleteChatShouldWorkCorrectly() {
        stubFor(delete("/tg-chat/1").willReturn(ok()
            .withHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)));
        assertThatNoException().isThrownBy(() -> client.deleteChat(1));
    }

    @Test
    void getTrackedLinksShouldReturnCorrectResponse() {
        stubFor(get("/links").withHeader("Tg-Chat-Id", matching("1")).willReturn(ok()
            .withHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_VALUE)
            .withBody("{\"links\": [{\"id\":1, \"url\": \"http://example.com\"}], \"size\":1}")));

        var expectedResponse = new ListLinksResponse(
            List.of(new LinkResponse(1L, URI.create("http://example.com"))),
            1L
        );

        assertThat(client.getAllTrackedLinks(1)).isEqualTo(expectedResponse);
    }

}
