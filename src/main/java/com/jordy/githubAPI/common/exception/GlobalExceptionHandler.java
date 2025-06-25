package com.jordy.githubAPI.common.exception;


import com.jordy.githubAPI.common.dto.ErrorResponse;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Feign Client에서 발생하는 404 Not Found 예외를 처리
     */
    @ExceptionHandler(FeignException.NotFound.class)
    protected ResponseEntity<ErrorResponse> handleFeignNotFoundException(FeignException.NotFound e) {
        log.error("Feign a Not Found Exception", e);
        final ErrorResponse response = new ErrorResponse(ErrorCode.USER_NOT_FOUND);
        return new ResponseEntity<>(response, ErrorCode.USER_NOT_FOUND.getStatus());
    }

    /**
     * 비즈니스 에러 처리
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
