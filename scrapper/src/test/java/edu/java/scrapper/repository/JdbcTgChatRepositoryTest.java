package edu.java.scrapper.repository;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import org.junit.jupiter.api.Test;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public class JdbcTgChatRepositoryTest extends RepositoryTest {

    @Test
    void chatShouldBeRegisteredCorrectly() {
        long id = 123;
        tgChatRepository.add(new TgChat(id));
        assertThat(tgChatRepository.findById(id)).isNotEmpty();
    }

    @Test
    void chatShouldBeRemovedCorrectly() {
        long id = 123;
        tgChatRepository.add(new TgChat(id));
        assertThat(tgChatRepository.remove(new TgChat(id))).isTrue();
    }

    @Test
    void allChatsShouldBeFoundCorrectly() {
        for (int i = 0; i < 10; ++i) {
            tgChatRepository.add(new TgChat(i + 1L));
        }
        var chats = tgChatRepository.findAll().stream()
            .sorted(Comparator.comparing(TgChat::getChatId))
            .toList();

        for (int i = 0; i < 10; ++i) {
            var chat = chats.get(i);
            assertThat(chat.getChatId()).isEqualTo(i + 1);
        }
    }

    @Test
    void trackingChatsShouldBeFoundCorrectly() {
        var chat1 = tgChatRepository.add(new TgChat(1L));
        tgChatRepository.add(new TgChat(2L));
        var chat3 = tgChatRepository.add(new TgChat(3L));

        var link = linkRepository.add(new Link(
            URI.create("http://example.com"),
            LinkType.GITHUB_REPOSITORY,
            OffsetDateTime.now()
        ));

        trackInfoRepository.add(new TrackInfo(link, chat1));
        trackInfoRepository.add(new TrackInfo(link, chat3));

        var expectedChats = List.of(1L, 3L);
        var trackingChats = tgChatRepository.findAllTrackingChats(link).stream().map(TgChat::getChatId).toList();

        assertThat(trackingChats).containsExactlyInAnyOrderElementsOf(expectedChats);
    }

}
