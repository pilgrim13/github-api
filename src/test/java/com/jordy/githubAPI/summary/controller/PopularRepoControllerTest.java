package com.jordy.githubAPI.summary.controller;

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

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 웹 계층 테스트. PopularRepoController 대상.
@WebMvcTest(PopularRepoController.class)
class PopularRepoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // 컨트롤러가 의존하는 서비스 Mocking
    @MockBean
    private PopularRepoService popularRepoService;

    @Test
    @DisplayName("조직의 인기 저장소 목록 조회 성공")
    void getPopularRepos_Success() throws Exception {
        // given
        String owner = "apache";
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
    @DisplayName("존재하지 않는 조직을 조회하면 404 에러를 반환한다")
    void getPopularRepos_Fail_OrgNotFound() throws Exception {
        // given
        String owner = "non-exist-organization";
        int limit = 10;
        ErrorCode expectedError = ErrorCode.USER_NOT_FOUND; // 현재 핸들러는 USER_NOT_FOUND를 반환

        // Mocking: FeignException.NotFound 예외 발생 설정
        Request request = Request.create(Request.HttpMethod.GET, "/", Collections.emptyMap(), null, new RequestTemplate());
        given(popularRepoService.getPopularRepos(owner, limit))
                .willThrow(new FeignException.NotFound("Not Found", request, null, null));

        // when & then
        mockMvc.perform(get("/api/popular-repo")
                        .param("owner", owner)
                        .param("limit", String.valueOf(limit)))
                // HTTP Status 404 검증
                .andExpect(status().isNotFound())
                // ErrorResponse 내용 검증
                .andExpect(jsonPath("$.code").value(expectedError.getCode()))
                .andExpect(jsonPath("$.message").value(expectedError.getMessage()))
                .andDo(print());
    }
}