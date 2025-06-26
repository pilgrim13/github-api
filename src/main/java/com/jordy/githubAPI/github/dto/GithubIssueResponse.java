package com.jordy.githubAPI.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
public class GithubIssueResponse {
    private int number;
    private String title;
    private String state;
    private int comments;
    @JsonProperty("created_at")
    private ZonedDateTime createdAt;
    @JsonProperty("closed_at")
    private ZonedDateTime closedAt;
}