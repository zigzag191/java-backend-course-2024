package edu.java.bot.service;

import edu.java.bot.telegramapi.exceptions.InvalidUrlException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TestUserService implements UserService {

    private final Map<Long, Set<String>> userLinks = new HashMap<>();

    @Override
    public boolean registerUser(long chatId) {
        if (userLinks.get(chatId) == null) {
            userLinks.put(chatId, new HashSet<>());
            log.info("user from chat {} was registered", chatId);
            return true;
        }
        return false;
    }

    @Override
    public boolean trackLink(long chatId, String link) {
        try {
            new URI(link).toURL();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            throw new InvalidUrlException(e);
        }
        log.info("link {} was tracked by {}", link, chatId);
        return userLinks.get(chatId).add(link);
    }

    @Override
    public boolean untrackLink(long chatId, String link) {
        log.info("link {} was untracked by {}", link, chatId);
        return userLinks.get(chatId).remove(link);
    }

    @Override
    public Collection<String> getTrackedLinks(long chatId) {
        return userLinks.get(chatId);
    }

}
