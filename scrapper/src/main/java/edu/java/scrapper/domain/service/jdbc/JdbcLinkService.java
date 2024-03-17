package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TrackInfo;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.LinkValidator;
import edu.java.scrapper.domain.service.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.repository.JdbcLinkRepository;
import edu.java.scrapper.repository.JdbcTgChatRepository;
import edu.java.scrapper.repository.JdbcTrackInfoRepository;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcLinkRepository linkRepository;
    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcTrackInfoRepository trackInfoRepository;
    private final LinkValidator linkValidator;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        var link = linkRepository.findByUrl(url).orElseGet(() -> {
            var newLink = linkValidator.createLink(url);
            return linkRepository.add(newLink);
        });

        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        try {
            trackInfoRepository.add(new TrackInfo(link, chat));
            return link;
        } catch (DataIntegrityViolationException ex) {
            throw new LinkIsAlreadyTrackedException();
        }
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) {
        var link = linkRepository.findByUrl(url).orElseThrow(LinkIsNotTrackedException::new);
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        if (!trackInfoRepository.remove(new TrackInfo(link, chat))) {
            throw new LinkIsNotTrackedException();
        }
        return link;
    }

    @Override
    public List<Link> findLongestNotPolled(int limit) {
        return linkRepository.findLongestNotPolled(limit);
    }

    @Override
    public List<Link> listAllTrackedLinks(long tgChatId) {
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        return linkRepository.findAllTrackedLinks(chat);
    }

    @Override
    public void updateLastPolled(Link link) {
        linkRepository.updateLastPolled(link);
    }

}
