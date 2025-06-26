package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RepoInfo {
    private String name;
    private String description;
    private int forks;
    @JsonProperty("default_branch")
    private String defaultBranch;
    @JsonProperty("total_branches")
    private int totalBranches;
    @JsonProperty("total_commits")
    private int totalCommits;
    @JsonProperty("latest_commit_date_utc")
    private String latestCommitDateUtc; // String으로 우선 처리
    private long size;
}