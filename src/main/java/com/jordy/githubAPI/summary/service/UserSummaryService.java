package com.jordy.githubAPI.summary.service;

import com.jordy.githubAPI.github.client.GithubApiClient;
import com.jordy.githubAPI.github.dto.GithubRepoResponse;
import com.jordy.githubAPI.github.dto.GithubUserResponse;
import com.jordy.githubAPI.summary.dto.LanguageStat;
import com.jordy.githubAPI.summary.dto.UserProfileSummaryResponse;
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
        GithubUserResponse user = githubApiClient.getUser(username);
        List<GithubRepoResponse> repoList = githubApiClient.getUserRepos(username);
        List<LanguageStat> languageStatList = getLanguageStats(repoList);
        return UserProfileSummaryResponse.from(user, languageStatList);
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