package edu.java.scrapper.domain.service;

import edu.java.scrapper.domain.model.Link;
import java.net.URI;
import java.util.List;

public interface LinkService {

    Link add(long tgChatId, URI url);

    Link remove(long tgChatId, URI url);

    List<Link> findLongestNotPolled(int limit);

    List<Link> listAllTrackedLinks(long tgChatId);

    void updateLastPolled(Link link);

}
