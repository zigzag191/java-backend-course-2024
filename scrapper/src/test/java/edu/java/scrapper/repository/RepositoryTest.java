package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "app.scheduler.enable=false")
@TestPropertySource(locations = "classpath:application.yml")
public abstract class RepositoryTest extends IntegrationTest {

    @Autowired JdbcLinkRepository linkRepository;
    @Autowired JdbcTgChatRepository tgChatRepository;
    @Autowired JdbcTrackInfoRepository trackInfoRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanLinkTable() {
        jdbcTemplate.update("DELETE FROM track_info");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM tg_chat");
    }

}
