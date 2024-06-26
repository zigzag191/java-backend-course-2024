package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.model.TgChat;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
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

    public static final String FIND_ALL_QUERY = "SELECT * FROM link";
    public static final String FIND_BY_URL_QUERY = "SELECT * FROM link WHERE url = ?";
    public static final String FIND_LONGEST_NOT_POLLED_QUERY = "SELECT * FROM link ORDER BY last_polled LIMIT (?)";
    public static final String REMOVE_BY_URL_QUERY = "DELETE FROM link WHERE url = ?";
    public static final String UPDATE_LAST_POLLED_QUERY = "UPDATE link SET last_polled = ? WHERE link_id = ?";
    public static final String FIND_ALL_TRACKED_LINKS_QUERY = """
        SELECT l.*
        FROM link AS l
            INNER JOIN track_info AS t ON l.link_id = t.link_id
        WHERE t.tg_chat = ?
        """;

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert insert;

    public JdbcLinkRepository(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        this.insert = new SimpleJdbcInsert(dataSource)
            .withTableName("link")
            .usingColumns("url", "type", "last_polled")
            .withoutTableColumnMetaDataAccess()
            .usingGeneratedKeyColumns("link_id");
    }

    @SuppressWarnings("MagicNumber")
    public Link add(Link link) {
        var id = insert.executeAndReturnKey(Map.of(
            "url", link.getUrl().toString(),
            "type", link.getType().name(),
            "last_polled", link.getLastPolled()
        )).longValue();
        link.setLinkId(id);
        return link;
    }

    public List<Link> findAll() {
        return jdbcTemplate.query(
            FIND_ALL_QUERY,
            LINK_ROW_MAPPER
        );
    }

    public Optional<Link> findByUrl(URI url) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                FIND_BY_URL_QUERY,
                LINK_ROW_MAPPER,
                url.toString()
            ));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    public List<Link> findAllTrackedLinks(TgChat tgChat) {
        return jdbcTemplate.query(
            FIND_ALL_TRACKED_LINKS_QUERY,
            LINK_ROW_MAPPER,
            tgChat.getChatId()
        );
    }

    public List<Link> findLongestNotPolled(int limit) {
        return jdbcTemplate.query(
            FIND_LONGEST_NOT_POLLED_QUERY,
            LINK_ROW_MAPPER,
            limit
        );
    }

    public boolean removeByUrl(URI url) {
        int deleted = jdbcTemplate.update(
            REMOVE_BY_URL_QUERY,
            url.toString()
        );
        return deleted == 1;
    }

    public void updateLastPolled(Link link) {
        jdbcTemplate.update(
            UPDATE_LAST_POLLED_QUERY,
            link.getLastPolled(),
            link.getLinkId()
        );
    }

}
