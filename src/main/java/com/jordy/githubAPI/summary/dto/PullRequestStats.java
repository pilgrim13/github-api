package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PullRequestStats {
    @JsonProperty("total_prs")
    private int totalPrs;
    @JsonProperty("open_prs")
    private int openPrs;
    @JsonProperty("closed_prs")
    private int closedPrs;
    @JsonProperty("merged_prs")
    private int mergedPrs;
    @JsonProperty("avg_time_to_merge_pr_hours")
    private double avgTimeToMergePrHours;
    @JsonProperty("top_active_reviewers")
    private List<TopActiveReviewer> topActiveReviewers;
}