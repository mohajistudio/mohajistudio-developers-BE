package com.mohajistudio.developers.api.domain.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class EmailVerifyResponse {
    private LocalDateTime expiredAt;
}
