package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.common.exception.BusinessException;
import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.summary.dto.RepoSummaryResponse;
import com.jordy.githubAPI.summary.service.RepoSummaryService;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RepoSummaryController.class)
class RepoSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepoSummaryService repoSummaryService;

    @Test
    @DisplayName("저장소 활동 요약 조회 성공")
    void getRepoSummary_Success() throws Exception {
        // given
        String owner = "octocat";
        String repo = "Spoon-Knife";
        RepoSummaryResponse mockResponse = RepoSummaryResponse.builder()
                .owner(owner)
                .repo(repo)
                .build();

        // 서비스 동작 Mocking: 성공 응답 설정
        given(repoSummaryService.getRepoSummary(owner, repo)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/repos/{owner}/{repo}/summary", owner, repo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.owner").value(owner))
                .andExpect(jsonPath("$.repo").value(repo))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 저장소를 조회하면 REPOSITORY_NOT_FOUND 에러를 반환한다")
    void getRepoSummary_Fail_RepoNotFound() throws Exception {
        // given
        String owner = "non-exist-owner";
        String repo = "non-exist-repo";
        // 기대하는 에러 코드를 REPOSITORY_NOT_FOUND로 변경
        ErrorCode expectedError = ErrorCode.REPOSITORY_NOT_FOUND;

        // Mocking: BusinessException 발생 설정
        given(repoSummaryService.getRepoSummary(owner, repo))
                .willThrow(new BusinessException(expectedError));

        // when & then
        mockMvc.perform(get("/api/repos/{owner}/{repo}/summary", owner, repo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("API 요청 횟수 제한 초과 시 403 에러를 반환한다")
    void getRepoSummary_Fail_RateLimitExceeded() throws Exception {
        // given
        String owner = "octocat";
        String repo = "Spoon-Knife";
        ErrorCode expectedError = ErrorCode.API_RATE_LIMIT_EXCEEDED;

        // Mocking: Rate Limit 초과 상황을 재현하기 위해 응답 헤더를 포함하여 예외를 생성
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());
        Map<String, Collection<String>> headers = Collections.singletonMap("x-ratelimit-remaining", Collections.singleton("0"));

        given(repoSummaryService.getRepoSummary(owner, repo))
                .willThrow(new FeignException.Forbidden("Forbidden", request, null, headers));

        // when & then
        mockMvc.perform(get("/api/repos/{owner}/{repo}/summary", owner, repo))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}