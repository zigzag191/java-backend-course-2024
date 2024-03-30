package edu.java.scrapper.repository;

import edu.java.scrapper.RepositoryTest;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JdbcTrackInfoRepositoryTest extends RepositoryTest {

    @Test
    @Transactional
    @Rollback
    void trackingSameLinkMultipleTimesShouldThrow() {
        var link = linkRepository.add(new Link(
            URI.create("http://example.com"),
            LinkType.GITHUB_REPOSITORY,
            OffsetDateTime.now()
        ));
        var chat = new TgChat(123L);
        tgChatRepository.add(chat);
        trackInfoRepository.add(new TrackInfo(link, chat));
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> trackInfoRepository.add(new TrackInfo(link, chat)));
    }

    @Test
    @Transactional
    @Rollback
    void removingUntrackedLinkShouldReturnFalse() {
        var link = new Link(
            URI.create("http://example.com"),
            LinkType.GITHUB_REPOSITORY,
            OffsetDateTime.now()
        );
        var chat = new TgChat(123L);
        tgChatRepository.add(chat);
        assertThat(trackInfoRepository.remove(new TrackInfo(link, chat))).isFalse();
    }

}
