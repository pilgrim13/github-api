package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jordy.githubAPI.github.dto.GithubUserResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserProfileSummaryResponse {
    private String username;
    @JsonProperty("profile_url")
    private String profileUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;
    @JsonProperty("public_repos")
    private int publicRepos;
    @JsonProperty("language_distribution")
    private List<LanguageStat> languageDistribution;
    @JsonProperty("last_updated_utc")
    private LocalDateTime lastUpdatedUtc;

    public static UserProfileSummaryResponse from(GithubUserResponse user, List<LanguageStat> stats) {
        return UserProfileSummaryResponse.builder()
                .username(user.getLogin())
                .profileUrl(user.getHtmlUrl())
                .avatarUrl(user.getAvatarUrl())
                .publicRepos(user.getPublicRepos())
                .languageDistribution(stats)
                .lastUpdatedUtc(LocalDateTime.now())
                .build();
    }
}