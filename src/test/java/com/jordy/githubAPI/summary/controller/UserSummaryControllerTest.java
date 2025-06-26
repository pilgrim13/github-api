package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.summary.service.UserSummaryService;
import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
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

// 웹 계층 테스트. UserSummaryController 대상.
@WebMvcTest(UserSummaryController.class)
class UserSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 컨트롤러가 의존하는 서비스 Mocking
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
    @DisplayName("존재하지 않는 사용자를 조회하면 404 에러를 반환한다")
    void getProfileSummary_Fail_UserNotFound() throws Exception {
        // given
        String nonExistentUsername = "not-exist-user-1214";
        ErrorCode expectedError = ErrorCode.USER_NOT_FOUND;

        // Mocking: FeignException.NotFound 예외를 생성
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());

        // 서비스 동작 Mocking: FeignException.NotFound 예외 발생 설정
        given(userSummaryService.getUserProfileSummary(nonExistentUsername))
                .willThrow(new FeignException.NotFound("Not Found", request, null, null));

        // when & then
        mockMvc.perform(get("/api/users/{username}/profile-summary", nonExistentUsername))
                // HTTP Status 404 검증
                .andExpect(status().isNotFound())
                // ErrorResponse 내용 검증
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}