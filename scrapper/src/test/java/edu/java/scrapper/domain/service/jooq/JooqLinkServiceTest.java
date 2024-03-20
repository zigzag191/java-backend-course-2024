package edu.java.scrapper.domain.service.jooq;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JooqLinkServiceTest extends LinkServiceTestBase {

    @Autowired JooqLinkService jooqLinkService;

    @Override
    protected LinkService createLinkService() {
        return jooqLinkService;
    }

}
