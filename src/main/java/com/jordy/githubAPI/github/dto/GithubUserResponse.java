package com.jordy.githubAPI.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
public class GithubUserResponse {

    private String login;

    private String name;

    // JSON의 'snake_case' 필드를 자바의 'camelCase' 필드에 매핑
    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("public_repos")
    private int publicRepos;

}