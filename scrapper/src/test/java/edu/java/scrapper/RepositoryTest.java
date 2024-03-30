package edu.java.scrapper;

import edu.java.scrapper.repository.JdbcLinkRepository;
import edu.java.scrapper.repository.JdbcTgChatRepository;
import edu.java.scrapper.repository.JdbcTrackInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
public abstract class RepositoryTest extends IntegrationTest {

    @Autowired protected JdbcLinkRepository linkRepository;
    @Autowired protected JdbcTgChatRepository tgChatRepository;
    @Autowired protected JdbcTrackInfoRepository trackInfoRepository;
    @Autowired protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearTables() {
        jdbcTemplate.update("DELETE FROM track_info");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM tg_chat");
    }

}