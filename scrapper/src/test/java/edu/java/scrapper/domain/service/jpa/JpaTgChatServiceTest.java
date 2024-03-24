package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.TgChatServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaTgChatServiceTest extends TgChatServiceTestBase {

    @Autowired JpaTgChatService tgChatService;

    @Override
    protected TgChatService createTgChatService() {
        return tgChatService;
    }

}
