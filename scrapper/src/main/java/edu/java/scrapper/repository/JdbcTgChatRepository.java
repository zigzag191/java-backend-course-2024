package edu.java.scrapper.repository;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTgChatRepository {

    public static final RowMapper<TgChat> TG_CHAT_ROW_MAPPER =
        (resultSet, rowNumber) -> new TgChat(resultSet.getLong("chat_id"));

    public static final String ADD_QUERY = "INSERT INTO tg_chat VALUES (?)";
    public static final String REMOVE_QUERY = "DELETE FROM tg_chat WHERE chat_id = ?";
    public static final String FIND_ALL_QUERY = "SELECT * FROM tg_chat";
    public static final String FIND_BY_ID_QUERY = "SELECT * FROM tg_chat WHERE chat_id = ?";
    public static final String FIND_ALL_TRACKING_CHATS_QUERY = """
        SELECT t.*
        FROM tg_chat as t
            INNER JOIN track_info AS i ON t.chat_id = i.tg_chat
        WHERE i.link_id = ?
        """;

    private final JdbcTemplate jdbcTemplate;

    public TgChat add(TgChat tgChat) {
        jdbcTemplate.update(ADD_QUERY, tgChat.getChatId());
        return tgChat;
    }

    public boolean remove(TgChat tgChat) {
        int deleted = jdbcTemplate.update(
            REMOVE_QUERY,
            tgChat.getChatId()
        );
        return deleted == 1;
    }

    public List<TgChat> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, TG_CHAT_ROW_MAPPER);
    }

    public Optional<TgChat> findById(long chatId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                FIND_BY_ID_QUERY,
                TG_CHAT_ROW_MAPPER,
                chatId
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<TgChat> findAllTrackingChats(Link link) {
        return jdbcTemplate.query(
            FIND_ALL_TRACKING_CHATS_QUERY,
            TG_CHAT_ROW_MAPPER,
            link.getLinkId()
        );
    }

}
