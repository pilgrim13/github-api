package com.jordy.githubAPI.common.exception;


import com.jordy.githubAPI.common.dto.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Collection;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @RequestParam, @PathVariable 등에서 타입 변환 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("Method Argument Type Mismatch", e);
        final ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
        return new ResponseEntity<>(response, ErrorCode.INVALID_INPUT_VALUE.getStatus());
    }

    /**
     * Feign Client에서 발생하는 5xx 서버 에러를 처리
     * 외부 API 서버가 불안정할 때 발생
     */
    @ExceptionHandler({FeignException.ServiceUnavailable.class, FeignException.InternalServerError.class})
    protected ResponseEntity<ErrorResponse> handleFeignServerException(FeignException e) {
        log.error("Github Server Exception", e);
        final ErrorResponse response = new ErrorResponse(ErrorCode.EXTERNAL_API_UNAVAILABLE);
        return new ResponseEntity<>(response, ErrorCode.EXTERNAL_API_UNAVAILABLE.getStatus());
    }

    /**
     * Feign Client에서 발생하는 403 Forbidden 예외를 처리
     */
    @ExceptionHandler(FeignException.Forbidden.class)
    protected ResponseEntity<ErrorResponse> handleFeignForbiddenException(FeignException.Forbidden e) {
        // Rate Limit 인 경우
        if (isRateLimitExceeded(e)) {
            log.warn("Feign Forbidden Exception: API Rate Limit Exceeded", e);
            return new ResponseEntity<>(new ErrorResponse(ErrorCode.API_RATE_LIMIT_EXCEEDED), ErrorCode.API_RATE_LIMIT_EXCEEDED.getStatus());
        }
        // 그 외 기타
        log.error("Feign Forbidden Exception: Access Denied", e);
        return new ResponseEntity<>(new ErrorResponse(ErrorCode.FORBIDDEN_ACCESS), ErrorCode.FORBIDDEN_ACCESS.getStatus());
    }

    // github 가능한 남은 요청 수 확인
    private boolean isRateLimitExceeded(FeignException.Forbidden e) {
        Collection<String> remainingHeader = e.responseHeaders().get("x-ratelimit-remaining");
        return remainingHeader != null && !remainingHeader.isEmpty() && "0".equals(remainingHeader.iterator().next());
    }

    /**
     * 직접 정의한 비즈니스 예외를 처리합니다.
     * (USER_NOT_FOUND, REPOSITORY_NOT_FOUND 등이 모두 이 핸들러를 통해 처리)
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Exception", e);
        final ErrorCode errorCode = e.getErrorCode();
        final ErrorResponse response = new ErrorResponse(errorCode);
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        final ErrorResponse response = new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, ErrorCode.INTERNAL_SERVER_ERROR.getStatus());
    }
}
