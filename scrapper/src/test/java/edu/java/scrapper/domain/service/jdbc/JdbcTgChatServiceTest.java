package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.TgChatServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class JdbcTgChatServiceTest extends TgChatServiceTestBase {

    @Autowired JdbcTgChatService tgChatService;

    @Override
    protected TgChatService createTgChatService() {
        return tgChatService;
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jdbc");
    }

}
