package com.mohajistudio.developers.api.domain.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetNicknameRequest {
    @NotNull
    @NotEmpty
    private String nickname;
}
