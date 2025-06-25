package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.summary.UserSummaryService;
import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserSummaryController {

    private final UserSummaryService userSummaryService;

    /**
     * 특정 사용자의 프로필 요약 정보 조회
     *
     * @param username GitHub 사용자 이름
     * @return 사용자 프로필 요약 응답 객체
     */
    @GetMapping("/{username}/profile-summary")
    public UserProfileSummaryResponse getProfileSummary(@PathVariable String username) {
        return userSummaryService.getUserProfileSummary(username);
    }
}