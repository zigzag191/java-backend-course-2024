package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.linkupdater.GithubRepositoryUpdater;
import edu.java.scrapper.domain.service.linkupdater.LinkUpdaterManager;
import edu.java.scrapper.domain.service.linkupdater.StackoverflowQuestionUpdater;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true")
public class LinkProcessorsConfig {

    @Bean
    public LinkUpdaterManager linkUpdaterManager(
        StackoverflowQuestionUpdater stackoverflowQuestionProcessor,
        GithubRepositoryUpdater githubRepositoryProcessor
    ) {
        var linkProcessorManager = new LinkUpdaterManager();
        linkProcessorManager.addProcessor(LinkType.STACK_OVERFLOW_QUESTION, stackoverflowQuestionProcessor);
        linkProcessorManager.addProcessor(LinkType.GITHUB_REPOSITORY, githubRepositoryProcessor);
        return linkProcessorManager;
    }

}
