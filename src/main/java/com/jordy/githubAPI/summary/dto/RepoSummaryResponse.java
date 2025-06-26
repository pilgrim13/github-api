package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class RepoSummaryResponse {
    private String owner;
    private String repo;
    @JsonProperty("repo_info")
    private RepoInfo repoInfo;
    @JsonProperty("issue_stats")
    private IssueStats issueStats;
    @JsonProperty("pull_request_stats")
    private PullRequestStats pullRequestStats;
    @JsonProperty("last_updated_utc")
    private LocalDateTime lastUpdatedUtc;
}