package com.jordy.githubAPI.summary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TopActiveReviewer {
    private String username;
    @JsonProperty("reviewed_prs_count")
    private long reviewedPrsCount;
}