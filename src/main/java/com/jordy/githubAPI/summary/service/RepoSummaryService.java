package com.jordy.githubAPI.summary.service;

import com.jordy.githubAPI.github.client.GithubApiClient;
import com.jordy.githubAPI.github.dto.*;
import com.jordy.githubAPI.summary.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 특정 GitHub 저장소의 활동 정보를 분석하고 요약하는 서비스
 */
@Service
@RequiredArgsConstructor
public class RepoSummaryService {

    private final GithubApiClient githubApiClient;

    public RepoSummaryResponse getRepoSummary(String owner, String repo) {
        // Github 데이터 조회
        GithubRepoDetailResponse repoDetails = githubApiClient.getRepoDetails(owner, repo);
        List<GithubIssueResponse> issues = githubApiClient.getRepoIssues(owner, repo);
        List<GithubPullRequestResponse> pullRequests = githubApiClient.getRepoPullRequests(owner, repo);
        List<GithubBranchResponse> branches = githubApiClient.getRepoBranches(owner, repo);
        List<GithubCommitResponse> commits = githubApiClient.getRepoCommits(owner, repo);

        // 데이터 가공 및 통계 계산
        RepoInfo repoInfo = getRepoInfo(repoDetails, branches, commits);
        IssueStats issueStats = getIssueStats(issues);
        PullRequestStats prStats = getPullRequestStats(pullRequests);

        return RepoSummaryResponse.builder()
                .owner(owner)
                .repo(repo)
                .repoInfo(repoInfo)
                .issueStats(issueStats)
                .pullRequestStats(prStats)
                .lastUpdatedUtc(LocalDateTime.now())
                .build();
    }

    /**
     * 저장소 기본 정보 생성
     */
    private RepoInfo getRepoInfo(GithubRepoDetailResponse details, List<GithubBranchResponse> branches, List<GithubCommitResponse> commits) {
        // 최신 커밋 날짜 추출
        String latestCommitDate = "N/A";
        if (commits != null && !commits.isEmpty()) {
            Optional<ZonedDateTime> latestDate = commits.stream()
                    .map(c -> c.getCommit().getAuthor().getDate())
                    .filter(Objects::nonNull)
                    .max(ZonedDateTime::compareTo);
            latestCommitDate = latestDate.map(d -> d.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)).orElse("N/A");
        }

        return RepoInfo.builder()
                .name(details.getName())
                .description(details.getDescription())
                .forks(details.getForksCount())
                .defaultBranch(details.getDefaultBranch())
                .size(details.getSize())
                .totalBranches(branches != null ? branches.size() : 0)
                .totalCommits(commits != null ? commits.size() : 0) // per_page=100 제한 있음
                .latestCommitDateUtc(latestCommitDate)
                .build();
    }

    /**
     * 이슈 관련 통계 생성
     */
    private IssueStats getIssueStats(List<GithubIssueResponse> issues) {
        if (issues == null || issues.isEmpty()) {
            return IssueStats.builder().openIssues(0).closedIssues(0).totalIssues(0).avgTimeToCloseIssueHours(0).topCommentedIssues(Collections.emptyList()).build();
        }
        int totalIssues = issues.size();
        int openIssues = (int) issues.stream().filter(i -> "open".equals(i.getState())).count();

        // 닫힌 이슈들의 평균 처리 시간 계산 (시간 단위)
        List<GithubIssueResponse> closedIssuesList = issues.stream().filter(i -> "closed".equals(i.getState()) && i.getClosedAt() != null).toList();
        double avgHours = closedIssuesList.stream()
                .mapToLong(i -> Duration.between(i.getCreatedAt(), i.getClosedAt()).toHours())
                .average().orElse(0.0);

        // 댓글 수 기준 상위 5개 이슈 추출
        List<TopCommentedIssue> topIssues = issues.stream()
                .sorted(Comparator.comparingInt(GithubIssueResponse::getComments).reversed())
                .limit(5)
                .map(i -> new TopCommentedIssue(i.getNumber(), i.getTitle(), i.getComments()))
                .toList();

        return IssueStats.builder()
                .totalIssues(totalIssues)
                .openIssues(openIssues)
                .closedIssues(totalIssues - openIssues)
                .avgTimeToCloseIssueHours(Math.round(avgHours * 10.0) / 10.0)
                .topCommentedIssues(topIssues)
                .build();
    }

    /**
     * Pull Request 관련 통계 생성
     */
    private PullRequestStats getPullRequestStats(List<GithubPullRequestResponse> prs) {
        if (prs == null || prs.isEmpty()) {
            return PullRequestStats.builder().openPrs(0).closedPrs(0).mergedPrs(0).totalPrs(0).avgTimeToMergePrHours(0).topActiveReviewers(Collections.emptyList()).build();
        }
        int totalPrs = prs.size();
        int openPrs = (int) prs.stream().filter(p -> "open".equals(p.getState())).count();
        int closedPrs = (int) prs.stream().filter(p -> "closed".equals(p.getState())).count();

        // 병합된 PR들의 평균 처리 시간 계산
        List<GithubPullRequestResponse> mergedPrsList = prs.stream().filter(p -> p.getMergedAt() != null).toList();
        int mergedPrs = mergedPrsList.size();

        double avgMergeHours = mergedPrsList.stream()
                .mapToLong(p -> Duration.between(p.getCreatedAt(), p.getMergedAt()).toHours())
                .average().orElse(0.0);

        // 가장 많이 활동한 리뷰어 5명 추출
        Map<String, Long> reviewerCounts = prs.stream()
                .filter(p -> p.getRequestedReviewers() != null)
                .flatMap(p -> p.getRequestedReviewers().stream())   // 모든 PR의 리뷰어 목록을 하나의 스트림으로 평탄화
                .map(GithubPullRequestResponse.Reviewer::getLogin)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));    // 리뷰어 이름으로 그룹화하여 카운트

        List<TopActiveReviewer> topReviewers = reviewerCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())  // 리뷰 횟수(value) 기준으로 내림차순 정렬
                .limit(5)
                .map(entry -> new TopActiveReviewer(entry.getKey(), entry.getValue()))
                .toList();

        return PullRequestStats.builder()
                .totalPrs(totalPrs)
                .openPrs(openPrs)
                .closedPrs(closedPrs)
                .mergedPrs(mergedPrs)
                .avgTimeToMergePrHours(Math.round(avgMergeHours * 10.0) / 10.0)
                .topActiveReviewers(topReviewers)
                .build();
    }
}
