package com.jordy.githubAPI.github.client;

import com.jordy.githubAPI.config.GithubFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "github-api", url = "${github.api.url}", configuration = GithubFeignConfig.class)
public interface GithubApiClient {

    @GetMapping("/users/{username}")
    String getUser(@PathVariable("username") String username);
}