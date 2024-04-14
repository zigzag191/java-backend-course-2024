package edu.java.scrapper.configuration.database;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.jooq.JooqLinkService;
import edu.java.scrapper.domain.service.jooq.JooqTgChatService;
import org.jooq.DSLContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jooq")
public class JooqAccessConfig {

    @Bean
    public LinkService linkService(DSLContext context, LinkValidator linkValidator) {
        return new JooqLinkService(context, linkValidator);
    }

    @Bean
    public TgChatService tgChatService(DSLContext context) {
        return new JooqTgChatService(context);
    }

}
