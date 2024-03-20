package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.TgChatServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcTgChatServiceTest extends TgChatServiceTestBase {

    @Autowired JdbcTgChatService tgChatService;

    @Override
    protected TgChatService createTgChatService() {
        return tgChatService;
    }

}
