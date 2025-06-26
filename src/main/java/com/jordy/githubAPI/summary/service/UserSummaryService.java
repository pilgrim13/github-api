package com.jordy.githubAPI.summary.service;

import com.jordy.githubAPI.common.exception.BusinessException;
import com.jordy.githubAPI.common.exception.ErrorCode;
import com.jordy.githubAPI.github.client.GithubApiClient;
import com.jordy.githubAPI.github.dto.GithubRepoResponse;
import com.jordy.githubAPI.github.dto.GithubUserResponse;
import com.jordy.githubAPI.summary.dto.LanguageStat;
import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSummaryService {

    private final GithubApiClient githubApiClient;

    public UserProfileSummaryResponse getUserProfileSummary(String username) {
        try {
            GithubUserResponse user = githubApiClient.getUser(username);
            List<GithubRepoResponse> repos = githubApiClient.getUserRepos(username);
            List<LanguageStat> languageStats = getLanguageStats(repos);
            return UserProfileSummaryResponse.from(user, languageStats);
        } catch (FeignException.NotFound e) {
            // Feign 404 에러를 잡아서 USER_NOT_FOUND 비즈니스 예외로 전환
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
    }

    private List<LanguageStat> getLanguageStats(List<GithubRepoResponse> repos) {
        List<String> languageList = repos.stream()
                .map(GithubRepoResponse::getLanguage)
                .filter(Objects::nonNull)
                .toList();

        long total = languageList.size();
        if (total == 0) return List.of();

        Map<String, Long> languageCountMap = languageList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return languageCountMap.entrySet().stream()
                .map(entry -> {
                    double percentage = (double) entry.getValue() * 100 / total;
                    // 소수점 둘째에서 반올림
                    double roundedPercentage = Math.round(percentage * 10.0) / 10.0;
                    return new LanguageStat(entry.getKey(), roundedPercentage);
                })
                // 내림차순 정렬
                .sorted((s1, s2) -> Double.compare(s2.getPercentage(), s1.getPercentage()))
                .collect(Collectors.toList());
    }
}