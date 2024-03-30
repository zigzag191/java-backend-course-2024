package edu.java.scrapper;

import edu.java.scrapper.repository.JdbcLinkRepository;
import edu.java.scrapper.repository.JdbcTgChatRepository;
import edu.java.scrapper.repository.JdbcTrackInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(properties = "app.scheduler.enable=false")
@TestPropertySource(locations = "classpath:application.yml")
public abstract class RepositoryTest extends IntegrationTest {

    @Autowired protected JdbcLinkRepository linkRepository;
    @Autowired protected JdbcTgChatRepository tgChatRepository;
    @Autowired protected JdbcTrackInfoRepository trackInfoRepository;
    @Autowired protected JdbcTemplate jdbcTemplate;

}
