package edu.java.scrapper.domain.service;

import edu.java.scrapper.domain.model.Link;
import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.exception.UnsupportedResourceException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class LinkValidator {

    public static final int GITHUB_REPO_PATH_LENGTH = 3;
    public static final int STACKOVERFLOW_QUESTION_PATH_LENGTH = 3;
    private final Set<Validator> validators = new HashSet<>();

    public LinkValidator() {
        validators.add(url -> {
            var path = url.getPath().split("/");
            if (url.getHost().equals("github.com") && path.length == GITHUB_REPO_PATH_LENGTH) {
                return new Link(url, LinkType.GITHUB_REPOSITORY, OffsetDateTime.now());
            }
            return null;
        });
        validators.add(url -> {
            var path = url.getPath().split("/");
            if (url.getHost().equals("stackoverflow.com")
                && path.length == STACKOVERFLOW_QUESTION_PATH_LENGTH
                && path[1].equals("questions")
                && Long.parseLong(path[2]) > 0
            ) {
                return new Link(url, LinkType.STACK_OVERFLOW_QUESTION, OffsetDateTime.now());
            }
            return null;
        });
    }

    public Link createLink(URI url) {
        for (var validator : validators) {
            var result = validator.createLink(url);
            if (result != null) {
                return result;
            }
        }
        throw new UnsupportedResourceException();
    }

    @FunctionalInterface
    private interface Validator {

        default Link createLink(URI url) {
            try {
                return createLinkImpl(url);
            } catch (Exception ex) {
                return null;
            }
        }

        Link createLinkImpl(URI url);

    }

}
