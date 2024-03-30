package edu.java.scrapper.domain.service;

import edu.java.scrapper.RepositoryTest;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import edu.java.scrapper.domain.service.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class LinkServiceTestBase extends RepositoryTest {

    static final Long TEST_CHAT_ID = 123L;
    static final String BASE_TEST_URI_STRING = "https://stackoverflow.com/questions/";
    static final URI TEST_URI = URI.create(BASE_TEST_URI_STRING + "123");

    LinkService linkService;

    @BeforeAll
    void init() {
        linkService = createLinkService();
    }

    @Test
    @Transactional
    @Rollback
    void linkShouldBeTrackedCorrectly() {
        long id = TEST_CHAT_ID;
        tgChatRepository.add(new TgChat(id));

        var addedLink = linkService.add(id, TEST_URI);

        var allTrackInfo = trackInfoRepository.findAll();

        assertThat(allTrackInfo).hasSize(1);
        assertThat(allTrackInfo.getFirst().getTgChat().getChatId()).isEqualTo(id);
        assertThat(allTrackInfo.getFirst().getLink().getLinkId()).isEqualTo(addedLink.getLinkId());
    }

    @Test
    @Transactional
    @Rollback
    void trackingSameLinkShouldThrow() {
        long id = TEST_CHAT_ID;
        tgChatRepository.add(new TgChat(id));

        linkService.add(id, TEST_URI);
        assertThatExceptionOfType(LinkIsAlreadyTrackedException.class)
            .isThrownBy(() -> linkService.add(id, TEST_URI));
    }

    @Test
    @Transactional
    @Rollback
    void linkShouldBeUntrackedCorrectly() {
        var chat = tgChatRepository.add(new TgChat(TEST_CHAT_ID));

        var link = linkRepository.add(new Link(
            TEST_URI,
            LinkType.STACK_OVERFLOW_QUESTION,
            OffsetDateTime.now()
        ));

        trackInfoRepository.add(new TrackInfo(link, chat));

        linkService.remove(chat.getChatId(), link.getUrl());

        var allTrackInfo = trackInfoRepository.findAll();
        assertThat(allTrackInfo).isEmpty();
    }

    @Test
    @Transactional
    @Rollback
    void removingLinkThatWasNotTrackedShouldThrow() {
        long id = TEST_CHAT_ID;
        tgChatRepository.add(new TgChat(id));
        assertThatExceptionOfType(LinkIsNotTrackedException.class)
            .isThrownBy(() -> linkService.remove(id, TEST_URI));
    }

    @Test
    @Transactional
    @Rollback
    void allTrackedLinksShouldBeFoundCorrectly() {
        var chat = tgChatRepository.add(new TgChat(TEST_CHAT_ID));

        linkService.add(chat.getChatId(), URI.create(BASE_TEST_URI_STRING + "123"));
        linkService.add(chat.getChatId(), URI.create(BASE_TEST_URI_STRING + "124"));
        linkService.add(chat.getChatId(), URI.create(BASE_TEST_URI_STRING + "125"));
        linkService.add(chat.getChatId(), URI.create(BASE_TEST_URI_STRING + "126"));

        var allTrackedLinks = linkService.listAllTrackedLinks(chat.getChatId());

        var questionIds = allTrackedLinks.stream()
            .map(link -> link.getUrl().getPath().split("/")[2])
            .map(Integer::valueOf)
            .toList();

        assertThat(questionIds).containsExactlyInAnyOrder(123, 124, 125, 126);
    }

    @Test
    @Transactional
    @Rollback
    void lastPolledShouldBeUpdatedCorrectly() {
        tgChatRepository.add(new TgChat(TEST_CHAT_ID));
        var link = linkService.add(TEST_CHAT_ID, TEST_URI);

        var newLastPolled = OffsetDateTime.of(LocalDateTime.of(2000, 1, 1, 1, 1), ZoneOffset.UTC);
        link.setLastPolled(newLastPolled);

        linkService.updateLastPolled(link);

        var updatedLink = linkRepository.findByUrl(link.getUrl()).orElseThrow();

        assertThat(updatedLink.getLastPolled()).isEqualTo(newLastPolled);
    }

    @Test
    @Transactional
    @Rollback
    void longestNotPolledShouldBeFoundCorrectly() {
        var chat = tgChatRepository.add(new TgChat(TEST_CHAT_ID));

        int totalAdded = 14;
        for (int i = 0; i < totalAdded; ++i) {
            linkService.add(chat.getChatId(), URI.create(BASE_TEST_URI_STRING + (i + 1)));
        }

        int notPolledQueued = 6;
        var longestNotPolled = linkService.findLongestNotPolled(notPolledQueued);
        assertThat(longestNotPolled).hasSize(notPolledQueued);

        var mostRecentlyPolled = linkRepository.findAll().stream()
            .sorted(Comparator.comparing(Link::getLastPolled).reversed())
            .limit(totalAdded - notPolledQueued)
            .toList();

        assertThat(longestNotPolled).doesNotContainAnyElementsOf(mostRecentlyPolled);
    }

    protected abstract LinkService createLinkService();

}
