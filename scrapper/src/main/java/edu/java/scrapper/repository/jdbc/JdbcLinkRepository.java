package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import java.net.URI;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Log4j2
@Repository
@SuppressWarnings("MultipleStringLiterals")
public class JdbcLinkRepository {

    public static final RowMapper<Link> LINK_ROW_MAPPER = (resultSet, rowNumber) -> {
        var link = new Link();
        link.setLinkId(resultSet.getLong("link_id"));
        link.setUrl(URI.create(resultSet.getString("url")));
        link.setType(LinkType.valueOf(resultSet.getString("type")));
        link.setLastPolled(resultSet.getObject("last_polled", OffsetDateTime.class));
        return link;
    };

    private final JdbcTemplate jdbcTemplate;

    public JdbcLinkRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @SuppressWarnings("MagicNumber")
    public Link add(Link link) {
        var keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var ps = connection.prepareStatement(
                "INSERT INTO link (url, type, last_polled) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, link.getUrl().toString());
            ps.setString(2, link.getType().name());
            ps.setObject(3, link.getLastPolled());
            return ps;
        }, keyHolder);
        link.setLinkId(((Integer) keyHolder.getKeyList().getFirst().get("link_id")).longValue());
        return link;
    }

    public List<Link> findAll() {
        return jdbcTemplate.query(
            "SELECT * FROM link",
            LINK_ROW_MAPPER
        );
    }

    public Optional<Link> findByUrl(URI url) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                "SELECT * FROM link WHERE url = ?",
                LINK_ROW_MAPPER,
                url.toString()
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Link> findAllTrackedLinks(TgChat tgChat) {
        return jdbcTemplate.query(
            "SELECT l.* FROM link AS l INNER JOIN track_info AS t ON l.link_id = t.link_id WHERE t.tg_chat = ?",
            LINK_ROW_MAPPER,
            tgChat.getChatId()
        );
    }

    public List<Link> findLongestNotPolled(int limit) {
        return jdbcTemplate.query(
            "SELECT * FROM link ORDER BY last_polled LIMIT (?)",
            LINK_ROW_MAPPER,
            limit
        );
    }

    public boolean removeByUrl(URI url) {
        int deleted = jdbcTemplate.update(
            "DELETE FROM link WHERE link.url = ?",
            url.toString()
        );
        return deleted == 1;
    }

    public void updateLastPolled(Link link) {
        jdbcTemplate.update(
            "UPDATE link SET last_polled = ? WHERE link_id = ?",
            link.getLastPolled(),
            link.getLinkId()
        );
    }

}
