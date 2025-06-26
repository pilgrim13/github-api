package com.jordy.githubAPI.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GithubOrgDetailResponse {
    // 조직의 전체 공개 저장소 개수를 가져오기 위해 사용
    @JsonProperty("public_repos")
    private int publicRepos;
}