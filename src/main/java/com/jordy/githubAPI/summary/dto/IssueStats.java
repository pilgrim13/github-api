package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class IssueStats {
    @JsonProperty("total_issues")
    private int totalIssues;
    @JsonProperty("open_issues")
    private int openIssues;
    @JsonProperty("closed_issues")
    private int closedIssues;
    @JsonProperty("avg_time_to_close_issue_hours")
    private double avgTimeToCloseIssueHours;
    @JsonProperty("top_commented_issues")
    private List<TopCommentedIssue> topCommentedIssues;
}