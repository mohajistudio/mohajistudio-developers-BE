package com.mohajistudio.developers.common.dto.response;

import lombok.*;

@Getter
public class ErrorResponse {
    private final String code;
    private final String message;

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
