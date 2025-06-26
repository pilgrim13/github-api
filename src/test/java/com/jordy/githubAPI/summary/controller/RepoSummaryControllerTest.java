package com.jordy.githubAPI.summary.controller;

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

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 웹 계층 테스트. RepoSummaryController 대상.
@WebMvcTest(RepoSummaryController.class)
class RepoSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 컨트롤러가 의존하는 서비스 Mocking
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
    @DisplayName("존재하지 않는 저장소를 조회하면 404 에러를 반환한다")
    void getRepoSummary_Fail_RepoNotFound() throws Exception {
        // given
        String owner = "non-exist-owner";
        String repo = "non-exist-repo";
        ErrorCode expectedError = ErrorCode.USER_NOT_FOUND; // 또는 REPO_NOT_FOUND

        // Mocking: FeignException.NotFound 예외 발생 설정
        Request dummyRequest = Request.create(Request.HttpMethod.GET, "/dummy", Collections.emptyMap(), null, new RequestTemplate());
        given(repoSummaryService.getRepoSummary(owner, repo))
                .willThrow(new FeignException.NotFound("Not Found", dummyRequest, null, null));

        // when & then
        mockMvc.perform(get("/api/repos/{owner}/{repo}/summary", owner, repo))
                // HTTP Status 404 검증
                .andExpect(status().isNotFound())
                // ErrorResponse 내용 검증
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}