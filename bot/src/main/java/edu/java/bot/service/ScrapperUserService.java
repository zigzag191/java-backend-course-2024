package edu.java.bot.service;

import edu.java.bot.client.ScrapperClient;
import edu.java.bot.client.exception.BadScrapperApiRequestException;
import edu.java.bot.service.exception.UnsupportedUpdateTypeException;
import edu.java.bot.telegramapi.exception.UnsupportedResourceException;
import edu.java.bot.telegramapi.response.MarkdownMessage;
import edu.java.bot.telegramapi.response.TelegramMessageSender;
import edu.java.common.dto.linkupdate.GithubRepoUpdateInfo;
import edu.java.common.dto.linkupdate.LinkUpdateInfo;
import edu.java.common.dto.linkupdate.StackoverflowQuestionUpdateInfo;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class ScrapperUserService implements UserService {

    private final ScrapperClient scrapperClient;
    private final TelegramMessageSender messageSender;

    @Override
    public boolean registerUser(long chatId) {
        try {
            scrapperClient.registerChat(chatId);
            return true;
        } catch (BadScrapperApiRequestException ex) {
            if (ex.getStatusCode().value() == HttpStatus.CONFLICT.value()) {
                return false;
            }
            throw ex;
        }
    }

    @Override
    public boolean trackLink(long chatId, String link) throws URISyntaxException, MalformedURLException {
        try {
            var url = new URI(link).toURL();
            scrapperClient.trackLink(chatId, url.toString());
            return true;
        } catch (BadScrapperApiRequestException ex) {
            var code = ex.getStatusCode();
            if (code.equals(HttpStatus.CONFLICT)) {
                return false;
            }
            if (code.equals(HttpStatus.NOT_IMPLEMENTED)) {
                throw new UnsupportedResourceException();
            }
            throw ex;
        }
    }

    @Override
    public boolean untrackLink(long chatId, String link) {
        try {
            scrapperClient.untrackLink(chatId, link);
            return true;
        } catch (BadScrapperApiRequestException ex) {
            var code = ex.getStatusCode();
            if (code.equals(HttpStatus.CONFLICT) || code.equals(HttpStatus.NOT_FOUND)) {
                return false;
            }
            throw ex;
        }
    }

    @Override
    public Collection<String> getTrackedLinks(long chatId) {
        return scrapperClient.getAllTrackedLinks(chatId).links().stream()
            .map(r -> r.url().toString())
            .toList();
    }

    @Override
    public void sendUpdates(URI url, List<Long> chatIds, LinkUpdateInfo updateInfo) {
        switch (updateInfo) {
            case GithubRepoUpdateInfo info -> sendGithubRepoUpdate(url, chatIds, info);
            case StackoverflowQuestionUpdateInfo info -> sendStackoverflowQuestionUpdates(url, chatIds, info);
            default -> throw new UnsupportedUpdateTypeException();
        }
    }

    @Override
    public List<URI> getSupportedResources() {
        return scrapperClient.getSupportedResources().resources();
    }

    private void sendGithubRepoUpdate(URI url, List<Long> chatIds, GithubRepoUpdateInfo info) {
        var updates = info.getActivities().stream()
            .collect(Collectors.groupingBy(
                GithubRepoUpdateInfo.Activity::activityType,
                HashMap::new,
                Collectors.counting()
            ));
        var message = new MarkdownMessage("Обновления в репозитории ").bold(url.toString()).plain(":").newLine();
        for (var update : updates.entrySet()) {
            message.escape(update.getKey().getDescription());
            if (update.getValue() > 1) {
                message.plain(" - x").escape(update.getValue().toString());
            }
            message.newLine();
        }
        for (long chatId : chatIds) {
            messageSender.send(chatId, message);
        }
    }

    private void sendStackoverflowQuestionUpdates(URI url, List<Long> chatIds, StackoverflowQuestionUpdateInfo info) {
        var message = new MarkdownMessage("Обновления в вопросе ").bold(url.toString()).plain(":").newLine();
        if (!info.getNewAnswers().isEmpty()) {
            message.newLine().plain("Новые ответы:").newLine();
            for (var answer : info.getNewAnswers()) {
                message.bold(answer.toString()).newLine();
            }
        }
        if (!info.getNewComments().isEmpty()) {
            message.newLine().plain("Новые комментарии:").newLine();
            for (var comment : info.getNewComments()) {
                message.bold(comment.toString()).newLine();
            }
        }
        for (long chatId : chatIds) {
            messageSender.send(chatId, message);
        }
    }

}
