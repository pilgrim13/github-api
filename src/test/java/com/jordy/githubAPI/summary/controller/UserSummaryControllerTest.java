package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.common.exception.BusinessException;
import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
import com.jordy.githubAPI.summary.service.UserSummaryService;
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

@WebMvcTest(UserSummaryController.class)
class UserSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSummaryService userSummaryService;

    @Test
    @DisplayName("사용자 프로필 요약 조회 성공")
    void getProfileSummary_Success() throws Exception {
        // given
        String username = "octocat";
        UserProfileSummaryResponse mockResponse = UserProfileSummaryResponse.builder()
                .username(username)
                .build();

        // 서비스 동작 Mocking: 성공 응답 설정
        given(userSummaryService.getUserProfileSummary(username)).willReturn(mockResponse);


        // when & then
        mockMvc.perform(get("/api/users/{username}/profile-summary", username))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 사용자를 조회하면 USER_NOT_FOUND 에러를 반환한다")
    void getProfileSummary_Fail_UserNotFound() throws Exception {
        // given
        String nonExistentUsername = "not-exist-user-1214";
        ErrorCode expectedError = ErrorCode.USER_NOT_FOUND;

        // Mocking: FeignException 대신 BusinessException 발생 설정
        given(userSummaryService.getUserProfileSummary(nonExistentUsername))
                .willThrow(new BusinessException(expectedError));

        // when & then
        mockMvc.perform(get("/api/users/{username}/profile-summary", nonExistentUsername))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("API 요청 횟수 제한 초과 시 403 에러를 반환한다")
    void getProfileSummary_Fail_RateLimitExceeded() throws Exception {
        // given
        String username = "octocat";
        ErrorCode expectedError = ErrorCode.API_RATE_LIMIT_EXCEEDED;

        // Mocking: Rate Limit 초과 상황을 재현하기 위해 응답 헤더를 포함하여 예외를 생성
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());
        Map<String, Collection<String>> headers = Collections.singletonMap("x-ratelimit-remaining", Collections.singleton("0"));

        given(userSummaryService.getUserProfileSummary(username))
                .willThrow(new FeignException.Forbidden("Forbidden", request, null, headers));

        // when & then
        mockMvc.perform(get("/api/users/{username}/profile-summary", username))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}