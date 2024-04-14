package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.jpa.JpaLinkService;
import edu.java.scrapper.domain.service.jpa.JpaTgChatService;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaTgChatRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfig {

    @Bean
    public LinkService linkService(
        JpaLinkRepository linkRepository,
        JpaTgChatRepository tgChatRepository,
        LinkValidator linkValidator
    ) {
        return new JpaLinkService(linkRepository, tgChatRepository, linkValidator);
    }

    @Bean
    public TgChatService tgChatService(JpaTgChatRepository tgChatRepository, JpaLinkRepository linkRepository) {
        return new JpaTgChatService(tgChatRepository, linkRepository);
    }

}
