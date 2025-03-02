package com.mohajistudio.developers.common.handler;

import com.mohajistudio.developers.common.dto.response.ErrorResponse;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.common.service.DiscordWebHookService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final DiscordWebHookService discordWebHookService;

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e, HttpServletRequest httpServletRequest) {
        log.error("CustomException: ", e);
        ErrorResponse errorResponse = new ErrorResponse(e.getCode(), e.getMessage());

        discordWebHookService.sendWebHookAsync(httpServletRequest, String.valueOf(e.getStatus()), e.getCode(), e.getMessage(), e.getMessage());
        return new ResponseEntity<>(errorResponse, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest httpServletRequest) {
        log.error("MethodArgumentNotValidException: ", e);
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        final ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), e.getBindingResult());

        discordWebHookService.sendWebHookAsync(httpServletRequest, String.valueOf(errorCode.getStatus()), errorCode.getCode(), errorCode.getMessage(), e.getMessage());
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest httpServletRequest) {
        log.error("HttpMessageNotReadableException: ", e);
        ErrorCode errorCode = ErrorCode.HTTP_MESSAGE_NOT_READABLE;
        return getErrorResponse(httpServletRequest, errorCode, e.getMessage(), e);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingServletRequestParameterException(MissingServletRequestParameterException e, HttpServletRequest httpServletRequest) {
        log.error("MissingServletRequestParameterException: ", e);
        ErrorCode errorCode = ErrorCode.MISSING_PARAMETER;
        return getErrorResponse(httpServletRequest, errorCode, e.getMessage(), e);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    protected ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest httpServletRequest) {
        log.error("MaxUploadSizeExceededException: ", e);
        ErrorCode errorCode = ErrorCode.MAX_UPLOAD_SIZE_EXCEEDED;
        return getErrorResponse(httpServletRequest, errorCode, e.getMessage(), e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest httpServletRequest) {
        log.error("IllegalArgumentException: ", e);
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;
        return getErrorResponse(httpServletRequest, errorCode, e.getMessage(), e);
    }

    private ResponseEntity<ErrorResponse> getErrorResponse(HttpServletRequest httpServletRequest, ErrorCode errorCode, String message, Exception e) {
        final ErrorResponse errorResponse = new ErrorResponse(errorCode);

        discordWebHookService.sendWebHookAsync(httpServletRequest, String.valueOf(errorCode.getStatus()), errorCode.getCode(), errorCode.getMessage(), message);
        return new ResponseEntity<>(errorResponse, errorCode.getStatus());
    }
}