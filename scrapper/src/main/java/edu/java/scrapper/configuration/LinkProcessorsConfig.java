package edu.java.scrapper.configuration;

import edu.java.scrapper.domain.model.LinkType;
import edu.java.scrapper.domain.service.linkprocessor.GithubRepositoryProcessor;
import edu.java.scrapper.domain.service.linkprocessor.LinkProcessorManager;
import edu.java.scrapper.domain.service.linkprocessor.StackoverflowQuestionProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LinkProcessorsConfig {

    @Bean
    public LinkProcessorManager linkProcessorManager(
        StackoverflowQuestionProcessor stackoverflowQuestionProcessor,
        GithubRepositoryProcessor githubRepositoryProcessor
    ) {
        var linkProcessorManager = new LinkProcessorManager();
        linkProcessorManager.addProcessor(LinkType.STACK_OVERFLOW_QUESTION, stackoverflowQuestionProcessor);
        linkProcessorManager.addProcessor(LinkType.GITHUB_REPOSITORY, githubRepositoryProcessor);
        return linkProcessorManager;
    }

}
