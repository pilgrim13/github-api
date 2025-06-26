package com.jordy.githubAPI.summary.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopCommentedIssue {
    @JsonProperty("issue_number")
    private int issueNumber;
    private String title;
    @JsonProperty("comments_count")
    private int commentsCount;
}