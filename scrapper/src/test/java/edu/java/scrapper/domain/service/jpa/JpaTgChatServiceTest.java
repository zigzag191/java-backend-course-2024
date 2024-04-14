package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.TgChatServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class JpaTgChatServiceTest extends TgChatServiceTestBase {

    @Autowired JpaTgChatService tgChatService;

    @Override
    protected TgChatService createTgChatService() {
        return tgChatService;
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }

}
