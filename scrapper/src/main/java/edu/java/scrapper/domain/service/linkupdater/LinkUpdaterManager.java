package edu.java.scrapper.domain.service.linkupdater;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class LinkUpdaterManager {

    private final List<URI> supportedResources = new ArrayList<>();
    private final EnumMap<LinkType, LinkUpdater> processors = new EnumMap<>(LinkType.class);

    public void addProcessor(LinkType type, LinkUpdater processor) {
        processors.put(type, processor);
        supportedResources.add(processor.getSupportedResource());
    }

    public boolean sendUpdates(Link link) {
        var processor = processors.get(link.getType());
        if (processor == null) {
            throw new UnsupportedResourceException();
        }
        return processor.sendUpdates(link);
    }

    public List<URI> getSupportedResources() {
        return Collections.unmodifiableList(supportedResources);
    }

}
