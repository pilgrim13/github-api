package com.jordy.githubAPI.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "common.01", "입력 값이 올바르지 않습니다."),

    // 403 Forbidden
    API_RATE_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "common.02", "API 요청 횟수 제한을 초과했습니다. 잠시 후 다시 시도해주세요."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "common.03", "해당 리소스에 접근할 권한이 없습니다."),

    // 404 Not Found
    NOT_FOUND(HttpStatus.NOT_FOUND, "common.04", "존재하지 않는 리소스 입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user.01", "해당 사용자를 찾을 수 없습니다."),
    REPOSITORY_NOT_FOUND(HttpStatus.NOT_FOUND, "repo.01", "해당 저장소를 찾을 수 없습니다."),
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "org.01", "해당 조직을 찾을 수 없습니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "common.99", "서버에 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}