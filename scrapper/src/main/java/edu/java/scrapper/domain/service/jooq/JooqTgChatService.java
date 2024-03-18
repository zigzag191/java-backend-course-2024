package edu.java.scrapper.domain.service.jooq;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import static edu.java.scrapper.domain.jooq.Tables.LINK;
import static edu.java.scrapper.domain.jooq.Tables.TG_CHAT;
import static edu.java.scrapper.domain.jooq.Tables.TRACK_INFO;

@Service
@RequiredArgsConstructor
public class JooqTgChatService implements TgChatService {

    private final DSLContext context;

    @Override
    public void register(long tgChatId) {
        var chat = context.newRecord(TG_CHAT);
        chat.setChatId((int) tgChatId);
        if (chat.store() != 1) {
            throw new RuntimeException();
        }
    }

    @Override
    public void unregister(long tgChatId) {
        int deleted = context.deleteFrom(TG_CHAT).where(TG_CHAT.CHAT_ID.eq((int) tgChatId)).execute();
        if (deleted == 0) {
            throw new TgChatDoesNotExistException();
        }
    }

    @Override
    public List<TgChat> findAllTrackingChats(Link link) {
        return context.select(TG_CHAT.CHAT_ID)
            .from(TG_CHAT)
            .innerJoin(TRACK_INFO).on(TG_CHAT.CHAT_ID.eq(TRACK_INFO.TG_CHAT))
            .innerJoin(LINK).on(LINK.LINK_ID.eq(TRACK_INFO.LINK_ID))
            .where(LINK.URL.eq(link.getUrl().toString()))
            .fetch()
            .stream()
            .map(chat -> new TgChat(Long.valueOf(chat.getValue(TG_CHAT.CHAT_ID))))
            .toList();
    }

}
