package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.summary.dto.PopularRepoResponse;
import com.jordy.githubAPI.summary.service.PopularRepoService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인기 저장소 조회 API", description = "특정 조직의 인기 저장소 목록을 조회")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PopularRepoController {

    private final PopularRepoService popularRepoService;

    @GetMapping("/popular-repo")
    public PopularRepoResponse getPopularRepos(
            @Parameter(description = "GitHub 조직 이름", required = true, example = "microsoft") @RequestParam("owner") String owner,
            @Parameter(description = "조회할 상위 저장소 개수 (기본값 10)", example = "5") @RequestParam(defaultValue = "10", name = "limit") int limit
    ) {
        return popularRepoService.getPopularRepos(owner, limit);
    }
}