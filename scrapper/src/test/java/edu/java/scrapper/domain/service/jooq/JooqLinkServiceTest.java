package edu.java.scrapper.domain.service.jooq;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class JooqLinkServiceTest extends LinkServiceTestBase {

    @Autowired JooqLinkService jooqLinkService;

    @Override
    protected LinkService createLinkService() {
        return jooqLinkService;
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jooq");
    }

}
