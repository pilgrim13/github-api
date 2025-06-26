package com.jordy.githubAPI.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubRepoDetailResponse {
    private String name;
    private String description;
    @JsonProperty("forks_count")
    private int forksCount;
    @JsonProperty("default_branch")
    private String defaultBranch;
    private long size;
}