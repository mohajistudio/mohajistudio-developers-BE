package com.mohajistudio.developers.common.exception;

import com.mohajistudio.developers.common.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomException extends RuntimeException {
   private String code;
   private String message;
   private HttpStatus status;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        setCode(errorCode.getCode());
        setMessage(errorCode.getMessage());
        setStatus(errorCode.getStatus());
    }

    public CustomException(ErrorCode errorCode, String message) {
        super(message);
        setCode(errorCode.getCode());
        setMessage(message);
        setStatus(errorCode.getStatus());
    }
}
