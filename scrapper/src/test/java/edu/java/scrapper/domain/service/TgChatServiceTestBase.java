package edu.java.scrapper.domain.service;

import edu.java.scrapper.RepositoryTest;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import edu.java.scrapper.domain.service.exception.TgChatAlreadyExistsException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TgChatServiceTestBase extends RepositoryTest {

    static final Long TEST_CHAT_ID = 123L;

    TgChatService tgChatService;

    @BeforeAll
    void init() {
        tgChatService = createTgChatService();
    }

    @Test
    void chatShouldBeRegisteredCorrectly() {
        tgChatService.register(TEST_CHAT_ID);
        var allChats = tgChatRepository.findAll();
        assertThat(allChats).hasSize(1);
        assertThat(allChats.getFirst().getChatId()).isEqualTo(TEST_CHAT_ID);
    }

    @Test
    void duplicateChatRegistrationShouldThrow() {
        tgChatService.register(TEST_CHAT_ID);
        assertThatExceptionOfType(TgChatAlreadyExistsException.class)
            .isThrownBy(() -> tgChatService.register(TEST_CHAT_ID));
    }

    @Test
    void chatShouldBeUnregisteredCorrectly() {
        tgChatRepository.add(new TgChat(TEST_CHAT_ID));
        tgChatService.unregister(TEST_CHAT_ID);
        assertThat(tgChatRepository.findAll()).isEmpty();
    }

    @Test
    void unregisteringNotExistentChatShouldThrow() {
        assertThatExceptionOfType(TgChatDoesNotExistException.class)
            .isThrownBy(() -> tgChatService.unregister(TEST_CHAT_ID));
    }

    @Test
    void allTrackingChatsShouldBeFoundCorrectly() {
        var link = linkRepository.add(new Link(
            URI.create("https://example.com"),
            LinkType.STACK_OVERFLOW_QUESTION,
            OffsetDateTime.now()
        ));

        for (long i = 1; i < 5; ++i) {
            tgChatService.register(i);
            trackInfoRepository.add(new TrackInfo(link, new TgChat(i)));
        }

        var allTrackingChats = tgChatService.findAllTrackingChats(link).stream()
            .map(TgChat::getChatId)
            .toList();

        assertThat(allTrackingChats).containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }

    protected abstract TgChatService createTgChatService();

}
