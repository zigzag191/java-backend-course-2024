package edu.java.common.dto.linkupdate;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = StackoverflowQuestionUpdateInfo.class, name = "STACKOVERFLOW_QUESTION_UPDATE"),
    @JsonSubTypes.Type(value = GithubRepoUpdateInfo.class, name = "GITHUB_UPDATE_INFO")
})
public abstract class LinkUpdateInfo {
}
