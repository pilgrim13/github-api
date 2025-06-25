package com.jordy.githubAPI.github.client;

import com.jordy.githubAPI.config.GithubFeignConfig;
import com.jordy.githubAPI.github.dto.GithubRepoResponse;
import com.jordy.githubAPI.github.dto.GithubUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "github-api", url = "${github.api.url}", configuration = GithubFeignConfig.class)
public interface GithubApiClient {

    @GetMapping("/users/{username}")
    GithubUserResponse getUser(@PathVariable("username") String username);

    // 사용자의 모든 Public Repository 목록 조회 (Page : 100개)
    @GetMapping("/users/{username}/repos?per_page=100")
    List<GithubRepoResponse> getUserRepos(@PathVariable("username") String username);
}