package edu.java.scrapper.domain.service.jooq;

import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.TgChatServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JooqTgChatServiceTest extends TgChatServiceTestBase {

    @Autowired JooqTgChatService tgChatService;

    @Override
    protected TgChatService createTgChatService() {
        return tgChatService;
    }

}
