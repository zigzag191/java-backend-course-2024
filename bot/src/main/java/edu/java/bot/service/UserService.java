package edu.java.bot.service;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;

public interface UserService {

    boolean registerUser(long chatId);

    boolean trackLink(long chatId, String link) throws URISyntaxException, MalformedURLException;

    boolean untrackLink(long chatId, String link);

    Collection<String> getTrackedLinks(long chatId);

}
