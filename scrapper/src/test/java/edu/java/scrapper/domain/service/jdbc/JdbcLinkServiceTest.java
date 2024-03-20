package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkServiceTestBase;
import org.springframework.beans.factory.annotation.Autowired;

public class JdbcLinkServiceTest extends LinkServiceTestBase {

    @Autowired JdbcLinkService linkService;

    @Override
    protected LinkService createLinkService() {
        return linkService;
    }

}
