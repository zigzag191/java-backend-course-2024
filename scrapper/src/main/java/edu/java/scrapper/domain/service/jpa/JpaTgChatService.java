package edu.java.scrapper.domain.service.jpa;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import edu.java.scrapper.domain.service.TgChatService;
import edu.java.scrapper.domain.service.exception.LinkIsNotTrackedException;
import edu.java.scrapper.domain.service.exception.TgChatAlreadyExistsException;
import edu.java.scrapper.domain.service.exception.TgChatDoesNotExistException;
import edu.java.scrapper.domain.service.jpa.entity.TgChatEntity;
import edu.java.scrapper.repository.jpa.JpaLinkRepository;
import edu.java.scrapper.repository.jpa.JpaTgChatRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(prefix = "app", name = "create-all-services", havingValue = "true")
@RequiredArgsConstructor
public class JpaTgChatService implements TgChatService {

    private final JpaTgChatRepository tgChatRepository;
    private final JpaLinkRepository linkRepository;

    @Override
    @Transactional
    public void register(long tgChatId) {
        if (tgChatRepository.existsById(tgChatId)) {
            throw new TgChatAlreadyExistsException();
        }
        tgChatRepository.save(new TgChatEntity((int) tgChatId));
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) {
        var chat = tgChatRepository.findById(tgChatId).orElseThrow(TgChatDoesNotExistException::new);
        tgChatRepository.delete(chat);
    }

    @Override
    @Transactional
    public List<TgChat> findAllTrackingChats(Link link) {
        return linkRepository.findByUrl(link.getUrl())
            .orElseThrow(LinkIsNotTrackedException::new)
            .getTrackingChats()
            .stream()
            .map(chat -> new TgChat((long) chat.getChatId()))
            .toList();
    }

}
