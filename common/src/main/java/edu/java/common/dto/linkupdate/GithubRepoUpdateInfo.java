package edu.java.common.dto.linkupdate;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubRepoUpdateInfo extends LinkUpdateInfo {

    List<Activity> activities;

    public record Activity(ActivityType activityType, OffsetDateTime timestamp) {}

    public enum ActivityType {
        PUSH,
        FORCE_PUSH,
        BRANCH_DELETION,
        BRANCH_CREATION,
        PR_MERGE,
        MERGE_QUEUE_MERGE
    }

}
