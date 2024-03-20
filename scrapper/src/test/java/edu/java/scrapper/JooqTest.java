package edu.java.scrapper;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.service.jooq.JooqLinkService;
import edu.java.scrapper.domain.service.jooq.JooqTgChatService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import java.net.URI;

@SpringBootTest(properties = "app.scheduler.enable=false")
@TestPropertySource(locations = "classpath:application.yml")
@Log4j2
public class JooqTest extends IntegrationTest {

    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired JooqLinkService linkService;
    @Autowired JooqTgChatService tgChatService;

    @BeforeEach
    void cleanLinkTable() {
        jdbcTemplate.update("DELETE FROM track_info");
        jdbcTemplate.update("DELETE FROM link");
        jdbcTemplate.update("DELETE FROM tg_chat");
    }

    @Test
    void test() {
        tgChatService.register(123);
        tgChatService.register(1);

        linkService.add(123, URI.create("https://stackoverflow.com/questions/123"));
        linkService.add(123, URI.create("https://stackoverflow.com/questions/124"));
        linkService.add(123, URI.create("https://stackoverflow.com/questions/125"));

        linkService.add(1, URI.create("https://stackoverflow.com/questions/124"));
        linkService.add(1, URI.create("https://stackoverflow.com/questions/123"));

        log.info(linkService.listAllTrackedLinks(123));
        log.info(tgChatService.findAllTrackingChats(new Link(
            URI.create("https://stackoverflow.com/questions/123"),
            null,
            null
        )));
    }

}
