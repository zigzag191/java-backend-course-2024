package edu.java.scrapper.domain.service.linkupdater;

import edu.java.scrapper.domain.model.Link;
import java.net.URI;

public interface LinkUpdater {

    boolean sendUpdates(Link link);

    URI getSupportedResource();

}
