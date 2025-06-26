package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.summary.dto.RepoSummaryResponse;
import com.jordy.githubAPI.summary.service.RepoSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
public class RepoSummaryController {

    private final RepoSummaryService repoSummaryService;

    @GetMapping("/{owner}/{repo}/summary")
    public RepoSummaryResponse getRepoSummary(@PathVariable String owner, @PathVariable String repo) {
        return repoSummaryService.getRepoSummary(owner, repo);
    }
}