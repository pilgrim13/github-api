package com.jordy.githubAPI.summary.controller;

import com.jordy.githubAPI.common.exception.BusinessException;
import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.summary.dto.PopularRepoResponse;
import com.jordy.githubAPI.summary.service.PopularRepoService;
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

@WebMvcTest(PopularRepoController.class)
class PopularRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PopularRepoService popularRepoService;

    @Test
    @DisplayName("조직의 인기 저장소 목록 조회 성공")
    void getPopularRepos_Success() throws Exception {
        // given
        String owner = "microsoft";
        int limit = 5;
        PopularRepoResponse mockResponse = PopularRepoResponse.builder()
                .targetOwner(owner)
                .build();

        // 서비스 동작 Mocking: 성공 응답 설정
        given(popularRepoService.getPopularRepos(owner, limit)).willReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                // DTO의 @JsonProperty("target_owner")에 맞게 jsonPath를 수정
                .andExpect(jsonPath("$.target_owner").value(owner))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 조직을 조회하면 ORGANIZATION_NOT_FOUND 에러를 반환한다")
    void getPopularRepos_Fail_OrgNotFound() throws Exception {
        // given
        String owner = "non-exist-organization";
        int limit = 10;
        // 기대하는 에러 코드를 ORGANIZATION_NOT_FOUND로 변경
        ErrorCode expectedError = ErrorCode.ORGANIZATION_NOT_FOUND;

        // Mocking: BusinessException 발생 설정
        given(popularRepoService.getPopularRepos(owner, limit))
                .willThrow(new BusinessException(expectedError));

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("API 요청 횟수 제한 초과 시 403 에러를 반환한다")
    void getPopularRepos_Fail_RateLimitExceeded() throws Exception {
        // given
        String owner = "apache";
        int limit = 5;
        ErrorCode expectedError = ErrorCode.API_RATE_LIMIT_EXCEEDED;

        // Mocking: Rate Limit 초과 상황을 재현하기 위해 응답 헤더를 포함하여 예외를 생성
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());
        Map<String, Collection<String>> headers = Collections.singletonMap("x-ratelimit-remaining", Collections.singleton("0"));

        given(popularRepoService.getPopularRepos(owner, limit))
                .willThrow(new FeignException.Forbidden("Forbidden", request, null, headers));

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", String.valueOf(limit)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("잘못된 타입의 파라미터 입력 시 400 에러를 반환한다")
    void getPopularRepos_Fail_InvalidInputType() throws Exception {
        // given
        String owner = "apache";
        String invalidLimit = "abc"; // int 타입이어야 할 자리에 문자열 입력
        ErrorCode expectedError = ErrorCode.INVALID_INPUT_VALUE;

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", invalidLimit)) // 잘못된 타입의 파라미터 전달
                // HTTP Status 400 검증
                .andExpect(status().isBadRequest())
                // ErrorResponse 내용 검증
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("외부 API 서버 오류 발생 시 503 에러를 반환한다")
    void getPopularRepos_Fail_ExternalApiDown() throws Exception {
        // given
        String owner = "apache";
        int limit = 5;
        ErrorCode expectedError = ErrorCode.EXTERNAL_API_UNAVAILABLE;

        // Mocking: FeignException.ServiceUnavailable (503) 예외 발생 설정
        Request dummyRequest = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());
        given(popularRepoService.getPopularRepos(owner, limit))
                .willThrow(new FeignException.ServiceUnavailable("Service Unavailable", dummyRequest, null, null));

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", String.valueOf(limit)))
                // HTTP Status 503 검증
                .andExpect(status().isServiceUnavailable())
                // ErrorResponse 내용 검증
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}