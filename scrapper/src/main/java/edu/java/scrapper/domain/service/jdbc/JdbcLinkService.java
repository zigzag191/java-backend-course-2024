package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TrackInfo;
import edu.java.scrapper.domain.service.LinkService;
import edu.java.scrapper.domain.service.exception.LinkIsAlreadyTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.domain.service.linkprocessor.LinkProcessorManager;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.repository.jdbc.JdbcTrackInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcLinkService implements LinkService {

    private final JdbcLinkRepository linkRepository;
    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcTrackInfoRepository trackInfoRepository;
    private final LinkProcessorManager linkProcessorManager;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) {
        var link = linkRepository.findByUrl(url).orElseGet(() -> {
            var newLink = linkProcessorManager.createLink(url);
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

}
