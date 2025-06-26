package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PopularRepoResponse {
    @JsonProperty("target_owner")
    private String targetOwner;
    @JsonProperty("total_public_repos")
    private int totalPublicRepos;
    @JsonProperty("popular_repos")
    private List<PopularRepoInfo> popularRepos;
    @JsonProperty("last_updated_utc")
    private LocalDateTime lastUpdatedUtc;
}