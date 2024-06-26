package edu.java.scrapper.domain.service;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.TgChat;
import java.util.List;

public interface TgChatService {

    void register(long tgChatId);

    void unregister(long tgChatId);

    List<TgChat> findAllTrackingChats(Link link);

}
