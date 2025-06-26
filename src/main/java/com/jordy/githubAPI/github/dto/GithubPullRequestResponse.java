package com.jordy.githubAPI.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class GithubPullRequestResponse {
    private String state;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
    @JsonProperty("merged_at")
    private ZonedDateTime mergedAt;
    @JsonProperty("requested_reviewers")
    private List<Reviewer> requestedReviewers;

    @Getter
    @NoArgsConstructor
    public static class Reviewer {
        private String login;
    }
}