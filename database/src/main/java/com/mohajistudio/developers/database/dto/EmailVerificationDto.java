package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
