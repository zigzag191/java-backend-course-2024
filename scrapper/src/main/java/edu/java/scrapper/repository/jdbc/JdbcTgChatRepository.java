package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcTgChatRepository {

    public static final RowMapper<TgChat> TG_CHAT_ROW_MAPPER =
        (resultSet, rowNumber) -> new TgChat(resultSet.getLong("chat_id"));

    private final JdbcTemplate jdbcTemplate;

    public JdbcTgChatRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public TgChat add(TgChat tgChat) {
        jdbcTemplate.update("INSERT INTO tg_chat VALUES (?)", tgChat.getChatId());
        return tgChat;
    }

    public boolean remove(TgChat tgChat) {
        int deleted = jdbcTemplate.update(
            "DELETE FROM tg_chat WHERE chat_id = ?",
            tgChat.getChatId()
        );
        return deleted == 1;
    }

    public List<TgChat> findAll() {
        return jdbcTemplate.query("SELECT * FROM tg_chat", TG_CHAT_ROW_MAPPER);
    }

    public Optional<TgChat> findById(long chatId) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM tg_chat WHERE chat_id = ?",
                TG_CHAT_ROW_MAPPER,
                chatId
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<TgChat> findAllTrackingChats(Link link) {
        return jdbcTemplate.query(
            "SELECT t.* FROM tg_chat as t INNER JOIN track_info AS i ON t.chat_id = i.tg_chat WHERE i.link_id = ?",
            TG_CHAT_ROW_MAPPER,
            link.getLinkId()
        );
    }

}
