package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.summary.dto.RepoSummaryResponse;
import com.jordy.githubAPI.summary.service.RepoSummaryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "저장소 요약 정보 조회 API", description = "특정 저장소의 요약 정보를 조회")
@RestController
@RequestMapping("/api/repos")
@RequiredArgsConstructor
public class RepoSummaryController {

    private final RepoSummaryService repoSummaryService;

    @GetMapping("/{owner}/{repo}/summary")
    public RepoSummaryResponse getRepoSummary(
            @Parameter(description = "저장소 소유자", required = true, example = "octocat") @PathVariable("owner") String owner,
            @Parameter(description = "저장소 이름", required = true, example = "Spoon-Knife") @PathVariable("repo") String repo) {
        return repoSummaryService.getRepoSummary(owner, repo);
    }
}