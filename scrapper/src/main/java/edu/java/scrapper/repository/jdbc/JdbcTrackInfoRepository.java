package edu.java.scrapper.repository.jdbc;

import edu.java.scrapper.domain.model.TrackInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcTrackInfoRepository {

    public static final RowMapper<TrackInfo> TRACK_INFO_ROW_MAPPER = (resultSet, rowNumber) -> {
        var trackInfo = new TrackInfo();
        trackInfo.setLink(JdbcLinkRepository.LINK_ROW_MAPPER.mapRow(resultSet, rowNumber));
        trackInfo.setTgChat(JdbcTgChatRepository.TG_CHAT_ROW_MAPPER.mapRow(resultSet, rowNumber));
        return trackInfo;
    };

    public static final String ADD_QUERY = "INSERT INTO track_info VALUES (?, ?)";
    public static final String REMOVE_QUERY = "DELETE FROM track_info WHERE link_id = ? AND tg_chat = ?";
    public static final String FIND_ALL_QUERY = """
        SELECT *
        FROM tg_chat AS t
            INNER JOIN track_info AS i ON t.chat_id = i.tg_chat
            INNER JOIN link AS l ON l.link_id = i.link_id
        """;

    private final JdbcTemplate jdbcTemplate;

    public void add(TrackInfo trackInfo) {
        jdbcTemplate.update(
            ADD_QUERY,
            trackInfo.getLink().getLinkId(),
            trackInfo.getTgChat().getChatId()
        );
    }

    public boolean remove(TrackInfo trackInfo) {
        int deleted = jdbcTemplate.update(
            REMOVE_QUERY,
            trackInfo.getLink().getLinkId(),
            trackInfo.getTgChat().getChatId()
        );
        return deleted == 1;
    }

    public List<TrackInfo> findAll() {
        return jdbcTemplate.query(
            FIND_ALL_QUERY,
            TRACK_INFO_ROW_MAPPER
        );
    }

}
