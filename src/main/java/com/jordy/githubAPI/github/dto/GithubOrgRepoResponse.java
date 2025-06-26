package com.jordy.githubAPI.github.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubOrgRepoResponse {
    private String name;
    private String description;
    @JsonProperty("stargazers_count")
    private int stargazersCount;
    @JsonProperty("forks_count")
    private int forksCount;
    private String language;
}