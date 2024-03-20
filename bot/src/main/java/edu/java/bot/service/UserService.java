package edu.java.bot.service;

import edu.java.common.dto.linkupdate.LinkUpdateInfo;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

public interface UserService {

    boolean registerUser(long chatId);

    boolean trackLink(long chatId, String link) throws URISyntaxException, MalformedURLException;

    boolean untrackLink(long chatId, String link);

    Collection<String> getTrackedLinks(long chatId);

    void sendUpdates(URI url, List<Long> chatIds, LinkUpdateInfo updateInfo);

}
