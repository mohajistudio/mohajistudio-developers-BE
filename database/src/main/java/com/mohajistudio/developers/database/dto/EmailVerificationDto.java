package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.VerificationType;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationDto {
    private UUID id;
    private String email;
    private String code;
    private int attempts;
    private VerificationType verificationType;
    private LocalDateTime verifiedAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @QueryProjection
    public EmailVerificationDto(UUID id, String email, String code, int attempts, VerificationType verificationType, LocalDateTime expiredAt, LocalDateTime verifiedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.code = code;
        this.attempts = attempts;
        this.verificationType = verificationType;
        this.verifiedAt = verifiedAt;
        this.expiredAt = expiredAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
