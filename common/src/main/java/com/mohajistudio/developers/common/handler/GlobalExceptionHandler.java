package com.mohajistudio.developers.common.handler;

import com.mohajistudio.developers.common.dto.response.ErrorResponse;
import com.mohajistudio.developers.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorResponse commonResponse = new ErrorResponse(e.getCode(), e.getMessage());

        return new ResponseEntity<>(commonResponse, e.getStatus());
    }
}
