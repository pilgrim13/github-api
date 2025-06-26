package com.jordy.githubAPI.github.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class GithubCommitResponse {
    private Commit commit;

    @Getter
    @NoArgsConstructor
    public static class Commit {
        private Author author;
    }

    @Getter
    @NoArgsConstructor
    public static class Author {
        private ZonedDateTime date;
    }
}