package edu.java.scrapper.domain.service.linkprocessor;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public class LinkProcessorManager {

    private final List<URI> supportedResources = new ArrayList<>();
    private final EnumMap<LinkType, LinkProcessor> processors = new EnumMap<>(LinkType.class);

    public void addProcessor(LinkType type, LinkProcessor processor) {
        processors.put(type, processor);
        supportedResources.add(processor.getSupportedResource());
    }

    public Link createLink(URI url) {
        for (var processor : processors.values()) {
            var result = processor.createLink(url);
            if (result != null) {
                return result;
            }
        }
        throw new UnsupportedResourceException();
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
