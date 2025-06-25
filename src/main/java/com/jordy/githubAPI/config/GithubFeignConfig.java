package com.jordy.githubAPI.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GithubFeignConfig {

    private final GithubProperty githubProperty;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 요청에 PAT 토큰 주입
                template.header("Authorization", "Bearer " + githubProperty.getToken());
                // GitHub API v3 사용을 위한 Accept 헤더를 추가
                template.header("Accept", "application/vnd.github+json");
            }
        };
    }
}