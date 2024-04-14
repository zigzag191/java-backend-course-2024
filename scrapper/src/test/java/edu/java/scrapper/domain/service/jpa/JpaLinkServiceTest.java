package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

public class JpaLinkServiceTest extends LinkServiceTestBase {

    @Autowired JpaLinkService linkService;

    @Override
    protected LinkService createLinkService() {
        return linkService;
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("app.database-access-type", () -> "jpa");
    }

}
