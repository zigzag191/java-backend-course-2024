package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.domain.service.jpa.entity.LinkEntity;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaTgChatRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class JpaLinkService implements LinkService {

    private final JpaLinkRepository linkRepository;
    private final JpaTgChatRepository tgChatRepository;
    private final LinkValidator linkValidator;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        var link = linkRepository.findByUrl(url).orElseGet(() -> {
            var newLink = linkValidator.createLink(url);
            return linkRepository.save(domainLinkToLinkEntity(newLink));
        });
        if (chat.getTrackedLinks().contains(link)) {
            throw new LinkIsAlreadyTrackedException();
        }
        chat.getTrackedLinks().add(link);
        return linkEntityToDomainLink(link);
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        var link = linkRepository.findByUrl(url).orElseThrow(LinkIsNotTrackedException::new);
        if (!chat.getTrackedLinks().remove(link)) {
            throw new LinkIsNotTrackedException();
        }
        return linkEntityToDomainLink(link);
    }

    @Override
    @Transactional
    public List<Link> findLongestNotPolled(int limit) {
        return linkRepository.findAllByOrderByLastPolled(Limit.of(limit)).stream()
            .map(this::linkEntityToDomainLink)
            .toList();
    }

    @Override
    @Transactional
    public List<Link> listAllTrackedLinks(long tgChatId) {
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        return chat.getTrackedLinks().stream()
            .map(this::linkEntityToDomainLink)
            .toList();
    }

    @Override
    @Transactional
    public void updateLastPolled(Link link) {
        var updatedLink = linkRepository.findById(link.getLinkId()).orElseThrow(LinkIsNotTrackedException::new);
        updatedLink.setLastPolled(link.getLastPolled());
        linkRepository.save(updatedLink);
    }

    private Link linkEntityToDomainLink(LinkEntity link) {
        return new Link((long) link.getLinkId(), link.getUrl(), link.getType(), link.getLastPolled());
    }

    private LinkEntity domainLinkToLinkEntity(Link link) {
        return new LinkEntity(link.getUrl(), link.getType(), link.getLastPolled());
    }

}
