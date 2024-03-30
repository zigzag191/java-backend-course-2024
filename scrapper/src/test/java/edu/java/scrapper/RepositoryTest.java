package edu.java.scrapper;

import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.repository.jdbc.JdbcTrackInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = {"app.scheduler.enable=false"})
@TestPropertySource(locations = "classpath:application.yml")
public abstract class RepositoryTest extends IntegrationTest {

    @Autowired protected JdbcLinkRepository linkRepository;
    @Autowired protected JdbcTgChatRepository tgChatRepository;
    @Autowired protected JdbcTrackInfoRepository trackInfoRepository;

    @Autowired protected JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanLinkTable() {
        jdbcTemplate.update("DELETE FROM track_info");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM tg_chat");
    }

}
