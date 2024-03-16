package edu.java.scrapper.domain.service.linkprocessor;

import edu.java.scrapper.domain.model.Link;
import java.net.URI;

public interface LinkProcessor {

    Link createLink(URI url);
    boolean sendUpdates(Link link);
    URI getSupportedResource();

}
