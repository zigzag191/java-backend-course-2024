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

    @Getter
    public enum ActivityType {

        PUSH("Новый push"),
        FORCE_PUSH("Новый force push"),
        BRANCH_DELETION("Создание ветки"),
        BRANCH_CREATION("Удаление ветки"),
        PR_MERGE("Слияние пул реквеста"),
        MERGE_QUEUE_MERGE("Слияние очереди слияния");

        private final String description;

        ActivityType(String description) {
            this.description = description;
        }

    }

}
