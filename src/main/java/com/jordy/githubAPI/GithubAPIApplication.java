package com.jordy.githubAPI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class GithubAPIApplication {
    public static void main(String[] args) {
        SpringApplication.run(GithubAPIApplication.class, args);
    }
}
