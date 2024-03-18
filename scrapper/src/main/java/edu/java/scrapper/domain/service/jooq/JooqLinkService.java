package edu.java.scrapper.domain.service.jooq;

import edu.java.scrapper.domain.jooq.tables.records.LinkRecord;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static edu.java.scrapper.domain.jooq.Tables.TG_CHAT;
import static edu.java.scrapper.domain.jooq.Tables.TRACK_INFO;
import static edu.java.scrapper.domain.jooq.tables.Link.LINK;

@Service
@RequiredArgsConstructor
public class JooqLinkService implements LinkService {

    private final DSLContext context;
    private final LinkValidator linkValidator;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        try {
            if (!tgChatExists(tgChatId)) {
                throw new TgChatDoesNotExistException();
            }
            var link = findOrCreateLink(url);
            var trackInfo = context.newRecord(TRACK_INFO);
            trackInfo.setTgChat((int) tgChatId);
            trackInfo.setLinkId(link.getLinkId());
            trackInfo.store();
            return linkRecordToDomainLink(link);
        } catch (DuplicateKeyException ex) {
            throw new LinkIsAlreadyTrackedException();
        }
    }

    @Override
    public Link remove(long tgChatId, URI url) {
        if (!tgChatExists(tgChatId)) {
            throw new TgChatDoesNotExistException();
        }
        var link = context.fetchOne(LINK, LINK.URL.eq(url.toString()));
        if (link == null) {
            throw new LinkIsNotTrackedException();
        }
        int deleted = context.deleteFrom(TRACK_INFO)
            .where(TRACK_INFO.LINK_ID.eq(link.getLinkId())).and(TRACK_INFO.TG_CHAT.eq((int) tgChatId))
            .execute();
        if (deleted == 0) {
            throw new LinkIsNotTrackedException();
        }
        return linkRecordToDomainLink(link);
    }

    @Override
    public List<Link> findLongestNotPolled(int limit) {
        return context.select(LINK).orderBy(LINK.LAST_POLLED.desc()).limit(limit)
            .stream()
            .map(this::linkRecordToDomainLink)
            .toList();
    }

    @Override
    public List<Link> listAllTrackedLinks(long tgChatId) {
        if (!tgChatExists(tgChatId)) {
            throw new TgChatDoesNotExistException();
        }
        return context.select(LINK.fields())
            .from(LINK)
            .innerJoin(TRACK_INFO).on(LINK.LINK_ID.eq(TRACK_INFO.LINK_ID))
            .where(TRACK_INFO.TG_CHAT.eq((int) tgChatId))
            .fetch().stream()
            .map(this::linkRecordToDomainLink)
            .toList();
    }

    @Override
    public void updateLastPolled(Link link) {
        context.update(LINK).set(LINK.LAST_POLLED, link.getLastPolled()).where(LINK.URL.eq(link.getUrl().toString()));
    }

    private boolean tgChatExists(long tgChatId) {
        return context.fetchExists(context.selectOne().from(TG_CHAT).where(TG_CHAT.CHAT_ID.eq((int) tgChatId)));
    }

    private Link linkRecordToDomainLink(Record link) {
        return new Link(
            URI.create(link.getValue(LINK.URL)),
            LinkType.valueOf(link.getValue(LINK.TYPE)),
            link.getValue(LINK.LAST_POLLED)
        );
    }

    private LinkRecord findOrCreateLink(URI url) {
        var link = context.fetchOne(LINK, LINK.URL.eq(url.toString()));
        if (link == null) {
            var newLink = linkValidator.createLink(url);
            var jooqLink = context.newRecord(LINK);
            jooqLink.setUrl(newLink.getUrl().toString());
            jooqLink.setType(newLink.getType().name());
            jooqLink.setLastPolled(newLink.getLastPolled());
            jooqLink.store();
            return jooqLink;
        } else {
            return link;
        }
    }

}
