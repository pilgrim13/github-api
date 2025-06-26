package com.jordy.githubAPI.github.client;

import com.jordy.githubAPI.config.GithubFeignConfig;
import com.jordy.githubAPI.github.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "github-api", url = "${github.api.url}", configuration = GithubFeignConfig.class)
public interface GithubApiClient {

    @GetMapping("/users/{username}")
    GithubUserResponse getUser(@PathVariable("username") String username);

    // 사용자의 모든 Public Repository 목록 조회 (Page : 100개)
    @GetMapping("/users/{username}/repos?per_page=100")
    List<GithubRepoResponse> getUserRepos(@PathVariable("username") String username);

    @GetMapping("/repos/{owner}/{repo}")
    GithubRepoDetailResponse getRepoDetails(@PathVariable("owner") String owner, @PathVariable("repo") String repo);

    // 이슈 목록 (최신 100개, 닫힌 이슈 포함)
    @GetMapping("/repos/{owner}/{repo}/issues?state=all&per_page=100")
    List<GithubIssueResponse> getRepoIssues(@PathVariable("owner") String owner, @PathVariable("repo") String repo);

    // PR 목록 (최신 100개, 닫힌 PR 포함)
    @GetMapping("/repos/{owner}/{repo}/pulls?state=all&per_page=100")
    List<GithubPullRequestResponse> getRepoPullRequests(@PathVariable("owner") String owner, @PathVariable("repo") String repo);

    // 브랜치 목록 (수량 파악용)
    @GetMapping("/repos/{owner}/{repo}/branches?per_page=100")
    List<GithubBranchResponse> getRepoBranches(@PathVariable("owner") String owner, @PathVariable("repo") String repo);

    // 커밋 목록 (수량 및 최신 날짜 파악용)
    @GetMapping("/repos/{owner}/{repo}/commits?per_page=100")
    List<GithubCommitResponse> getRepoCommits(@PathVariable("owner") String owner, @PathVariable("repo") String repo);

    // 특정 조직의 상세 정보를 조회
    @GetMapping("/orgs/{org}")
    GithubOrgDetailResponse getOrgDetails(@PathVariable("org") String org);

    //특정 조직의 저장소 목록을 스타 순으로 정렬하여 조회
    @GetMapping("/orgs/{org}/repos?sort=stars")
    List<GithubOrgRepoResponse> getPopularOrgRepos(@PathVariable("org") String org, @RequestParam("per_page") int limit);

}