package com.jordy.githubAPI.summary.service;


import com.jordy.githubAPI.common.exception.BusinessException;
import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.github.client.GithubApiClient;
import com.jordy.githubAPI.github.dto.GithubOrgDetailResponse;
import com.jordy.githubAPI.github.dto.GithubOrgRepoResponse;
import com.jordy.githubAPI.summary.dto.PopularRepoInfo;
import com.jordy.githubAPI.summary.dto.PopularRepoResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PopularRepoService {

    private final GithubApiClient githubApiClient;

    public PopularRepoResponse getPopularRepos(String owner, int limit) {
        try {
            // 조직의 전체 공개 저장소 개수 조회
            GithubOrgDetailResponse orgDetails = githubApiClient.getOrgDetails(owner);

            // 스타 순으로 정렬된 인기 저장소 목록 조회
            List<GithubOrgRepoResponse> orgRepos = githubApiClient.getPopularOrgRepos(owner, limit);

            // 조회된 데이터를 최종 응답 DTO로 변환
            List<PopularRepoInfo> popularRepos = (orgRepos != null)
                    ? orgRepos.stream().map(PopularRepoInfo::from).collect(Collectors.toList())
                    : Collections.emptyList();

            return PopularRepoResponse.builder()
                    .targetOwner(owner)
                    .totalPublicRepos(orgDetails.getPublicRepos())
                    .popularRepos(popularRepos)
                    .lastUpdatedUtc(LocalDateTime.now())
                    .build();
        } catch (FeignException.NotFound e) {
            // Feign 404 에러를 ORGANIZATION_NOT_FOUND 비즈니스 예외로 전환
            throw new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
    }

}
