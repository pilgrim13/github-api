package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.github.client.GithubApiClient;
import com.jordy.githubAPI.github.dto.GithubUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final GithubApiClient githubApiClient;

    @GetMapping("/user/{username}")
    public GithubUserResponse testGetUser(@PathVariable String username) {
        return githubApiClient.getUser(username);
    }
}