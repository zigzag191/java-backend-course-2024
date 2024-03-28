package edu.java.scrapper.repository;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class JdbcLinkRepositoryTest extends RepositoryTest {

    static final OffsetDateTime ZERO_TIMESTAMP = OffsetDateTime.ofInstant(Instant.ofEpochSecond(0), ZoneId.of("UTC"));

    @Test
    void linkShouldBeAddedCorrectly() {
        var url = URI.create("http://example.com");
        var addedLink = linkRepository.add(new Link(url, LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP));

        var expectedLink = new Link(url, LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP);
        expectedLink.setLinkId(addedLink.getLinkId());

        assertThat(linkRepository.findByUrl(url)).contains(expectedLink);
    }

    @Test
    void addingDuplicateUrlShouldThrow() {
        var link = new Link(URI.create("http://example.com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP);
        linkRepository.add(link);
        assertThatExceptionOfType(DataIntegrityViolationException.class)
            .isThrownBy(() -> linkRepository.add(link));
    }

    @Test
    void allLinkShouldBeFound() {
        for (int i = 0; i < 10; ++i) {
            linkRepository.add(new Link(
                URI.create("http://example" + (i + 1) + ".com"),
                LinkType.GITHUB_REPOSITORY,
                ZERO_TIMESTAMP
            ));
        }

        var links = linkRepository.findAll().stream()
            .sorted(Comparator.comparing(Link::getLinkId))
            .toList();

        assertThat(links).hasSize(10);

        for (int i = 0; i < 10; ++i) {
            var link = links.get(i);
            assertThat(link).extracting(Link::getUrl, Link::getType, Link::getLastPolled)
                .contains(URI.create("http://example" + (i + 1) + ".com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP);
        }
    }

    @Test
    void linkShouldBeRemovedCorrectly() {
        var link = new Link(URI.create("http://example.com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP);
        linkRepository.add(link);

        assertThat(linkRepository.removeByUrl(link.getUrl())).isTrue();
        assertThat(linkRepository.removeByUrl(link.getUrl())).isFalse();
    }

    @Test
    void trackedLinksShouldBeFoundCorrectly() {
        var chat = new TgChat(123L);
        tgChatRepository.add(chat);

        var link1 =
            linkRepository.add(new Link(URI.create("http://example1.com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP));
        var link2 =
            linkRepository.add(new Link(URI.create("http://example2.com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP));
        linkRepository.add(new Link(URI.create("http://example3.com"), LinkType.GITHUB_REPOSITORY, ZERO_TIMESTAMP));

        trackInfoRepository.add(new TrackInfo(link1, chat));
        trackInfoRepository.add(new TrackInfo(link2, chat));

        var expectedLinks = List.of("http://example1.com", "http://example2.com");
        var trackedLinks = linkRepository.findAllTrackedLinks(chat).stream()
            .map(link -> link.getUrl().toString())
            .toList();

        assertThat(trackedLinks).containsExactlyInAnyOrderElementsOf(expectedLinks);
    }

}
