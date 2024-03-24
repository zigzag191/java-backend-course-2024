package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JpaLinkServiceTest extends LinkServiceTestBase {

    @Autowired JpaLinkService linkService;

    @Override
    protected LinkService createLinkService() {
        return linkService;
    }

}
