package com.jordy.githubAPI.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class GithubProperty {

    private final String token;

    public GithubProperty(@Value("${github.token}") String token) {
        this.token = token;
    }
}
