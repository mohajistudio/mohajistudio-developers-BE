package com.mohajistudio.developers.common.dto.response;

import com.mohajistudio.developers.common.enums.ErrorCode;
import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;
    private final List<FieldError> errors;

    public ErrorResponse(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errors = new ArrayList<>();
    }

    public ErrorResponse(String code, String message, BindingResult bindingResult) {
        this.code = code;
        this.message = message;
        this.errors = bindingResult.getFieldErrors();
    }
}
