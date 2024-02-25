package edu.java.bot.service;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class TestUserService implements UserService {

    private final Map<Long, Set<String>> userLinks = new HashMap<>();

    @Override
    public boolean registerUser(long chatId) {
        if (!userLinks.containsKey(chatId)) {
            userLinks.put(chatId, new HashSet<>());
            log.info("user from chat {} was registered", chatId);
            return true;
        }
        return false;
    }

    @Override
    public boolean trackLink(long chatId, String link) throws URISyntaxException, MalformedURLException {
        Objects.requireNonNull(link);
        var url = new URI(link).toURL();
        boolean tracked = userLinks.get(chatId).add(url.toString());
        if (tracked) {
            log.info("link {} was tracked by {}", link, chatId);
        }
        return tracked;
    }

    @Override
    public boolean untrackLink(long chatId, String link) {
        Objects.requireNonNull(link);
        boolean untracked = userLinks.get(chatId).remove(link);
        if (untracked) {
            log.info("link {} was untracked by {}", link, chatId);
        }
        return untracked;
    }

    @Override
    public Collection<String> getTrackedLinks(long chatId) {
        return userLinks.get(chatId);
    }

}
