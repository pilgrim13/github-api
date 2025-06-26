package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jordy.githubAPI.github.dto.GithubOrgRepoResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopularRepoInfo {
    private String name;
    private String description;
    private int stars;
    private int forks;
    @JsonProperty("main_language")
    private String mainLanguage;

    // GithubOrgRepoResponse DTO를 최종 응답 DTO로 변환하는 정적 팩토리 메소드
    public static PopularRepoInfo from(GithubOrgRepoResponse repo) {
        return PopularRepoInfo.builder()
                .name(repo.getName())
                .description(repo.getDescription())
                .stars(repo.getStargazersCount())
                .forks(repo.getForksCount())
                .mainLanguage(repo.getLanguage())
                .build();
    }
}