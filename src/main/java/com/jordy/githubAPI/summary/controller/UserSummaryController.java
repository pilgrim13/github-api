package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
import com.jordy.githubAPI.summary.service.UserSummaryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "유저 프로필 조회 API", description = "특정 유저의 프로필 요약 정보 조회")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserSummaryController {

    private final UserSummaryService userSummaryService;

    @GetMapping("/{username}/profile-summary")
    public UserProfileSummaryResponse getProfileSummary(
            @Parameter(description = "Github 유저 이름", required = true, example = "octocat") @PathVariable("username") String username) {
        return userSummaryService.getUserProfileSummary(username);
    }
}