package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatAlreadyExistsException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.repository.jdbc.JdbcLinkRepository;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {

    private final JdbcTgChatRepository tgChatRepository;
    private final JdbcLinkRepository linkRepository;

    @Override
    public void register(long tgChatId) {
        try {
            tgChatRepository.add(new TgChat(tgChatId));
        } catch (DataIntegrityViolationException ex) {
            throw new TgChatAlreadyExistsException();
        }
    }

    @Override
    public void unregister(long tgChatId) {
        if (!tgChatRepository.remove(new TgChat(tgChatId))) {
            throw new TgChatDoesNotExistException();
        }
    }

    @Override
    public List<TgChat> listAllTrackingChats(URI url) {
        var link = linkRepository.findByUrl(url).orElseThrow(LinkIsNotTrackedException::new);
        return tgChatRepository.findAllTrackingChats(link);
    }

}
