package edu.java.bot.service;

import java.util.Collection;

public interface UserService {

    boolean registerUser(long chatId);

    boolean trackLink(long chatId, String link);

    boolean untrackLink(long chatId, String link);

    Collection<String> getTrackedLinks(long chatId);

}
