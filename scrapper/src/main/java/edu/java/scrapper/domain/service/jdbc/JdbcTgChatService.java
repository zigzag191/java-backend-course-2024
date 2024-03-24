package edu.java.scrapper.domain.service.jdbc;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.exception.TgChatAlreadyExistsException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.repository.jdbc.JdbcTgChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "app", name = "create-all-services", havingValue = "true")
@RequiredArgsConstructor
public class JdbcTgChatService implements TgChatService {

    private final JdbcTgChatRepository tgChatRepository;

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
    public List<TgChat> findAllTrackingChats(Link link) {
        return tgChatRepository.findAllTrackingChats(link);
    }

}
