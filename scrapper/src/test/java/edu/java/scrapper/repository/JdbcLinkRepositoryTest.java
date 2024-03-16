package edu.java.scrapper.repository;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.net.URI;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application.yml")
@Log4j2
public class JdbcLinkRepositoryTest extends IntegrationTest {

    @Autowired
    JdbcLinkRepository jdbcLinkRepository;

    @Test
    void test() {
        var link = new Link(URI.create("http://example.com"), LinkType.GITHUB_REPOSITORY, OffsetDateTime.now());
        jdbcLinkRepository.add(link);
        link.setLinkId(123L);
        jdbcLinkRepository.findByUrl(URI.create("http://example2.com"));
    }

}
