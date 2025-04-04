package com.mohajistudio.developers.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U0001", "알 수 없는 유저"),

    // Register
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "R0001", "이미 존재하는 유저"),
    PASSWORD_ALREADY_SET(HttpStatus.BAD_REQUEST, "R0002", "이미 설정된 비밀번호"),
    NICKNAME_ALREADY_SET(HttpStatus.BAD_REQUEST, "R0003", "이미 설정된 닉네임"),
    INCOMPLETE_REGISTRATION(HttpStatus.UNAUTHORIZED, "R0004", "회원가입이 완료되지 않은 유저"),
    PASSWORD_NOT_SET(HttpStatus.BAD_REQUEST, "R0005", "설정되지 않은 비밀번호"),
    NICKNAME_NOT_SET(HttpStatus.BAD_REQUEST, "R0006", "설정되지 않은 닉네임"),

    // Authentication
    PASSWORD_RESET_REQUIRED(HttpStatus.BAD_REQUEST, "A0001", "초기화 된 비밀번호로 재설정 필요"),

    // Email Verification
    EMAIL_SEND_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "EV0001", "이메일 전송 실패"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "EV0002", "유효하지 않은 이메일"),
    INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "EV0002", "유효하지 않은 인증 코드"),
    EXCEEDED_VERIFICATION_ATTEMPTS(HttpStatus.BAD_REQUEST, "EV0003", "이메일 인증 횟수 초과"),
    EMAIL_REQUEST_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "EV004", "인증 메일 요청 횟수 초과"),

    // JWT
    REFRESH_TOKEN_MISMATCH(HttpStatus.UNAUTHORIZED, "T0001", "유효하지 않은 토큰"),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "T0002", "유효하지 않은 토큰"),

    // Infra
    MULTIPART_FILE_EXCEPTION(HttpStatus.BAD_REQUEST, "MF0001", "파일을 찾을 수 없음"),
    STORAGE_UPLOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "MF0002", "스토리지 업로드 실패"),
    MAX_UPLOAD_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "MF0003", "파일 최대 크기 초과"),
    INVALID_MEDIA_TYPE(HttpStatus.BAD_REQUEST, "MF0004", "잘못된 파일 형식"),
    INVALID_MEDIA_FILE(HttpStatus.BAD_REQUEST, "MF0005", "잘못된 미디어 파일"),
    STORAGE_DELETE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "MF0006", "스토리지 삭제 실패"),

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C0001", "유효하지 않은 입력 값"),
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "C0002", "잘못된 JSON 요청 형식"),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "C0003", "필수 값인 매개변수를 찾을 수 없음"),
    ENTITY_NOT_FOUND(HttpStatus.BAD_REQUEST, "C0004", "알 수 없는 엔티티"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C0005", "권한 없음"),
    ILLEGAL_ARGUMENT(HttpStatus.BAD_REQUEST, "C0006", "잘못된 인자"),

    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C9999", "알 수 없는 에러");

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(final HttpStatus status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
