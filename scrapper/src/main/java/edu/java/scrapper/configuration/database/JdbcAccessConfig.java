package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.jdbc.JdbcLinkService;
import edu.java.scrapper.domain.service.jdbc.JdbcTgChatService;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.repository.jdbc.JdbcTrackInfoRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfig {

    @Bean
    public LinkService linkService(
        JdbcLinkRepository linkRepository,
        JdbcTgChatRepository tgChatRepository,
        JdbcTrackInfoRepository trackInfoRepository,
        LinkValidator linkValidator
    ) {
        return new JdbcLinkService(linkRepository, tgChatRepository, trackInfoRepository, linkValidator);
    }

    @Bean
    public TgChatService tgChatService(JdbcTgChatRepository tgChatRepository) {
        return new JdbcTgChatService(tgChatRepository);
    }

}
