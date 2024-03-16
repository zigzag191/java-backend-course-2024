package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.model.TrackInfo;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.repository.jdbc.JdbcTrackInfoRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.net.URI;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@Log4j2
public class JdbcTrackInfoRepositoryTest extends IntegrationTest {

    @Autowired
    JdbcLinkRepository jdbcLinkRepository;

    @Autowired
    JdbcTgChatRepository jdbcTgChatRepository;

    @Autowired
    JdbcTrackInfoRepository jdbcTrackInfoRepository;

}
