package com.mohajistudio.developers.api.domain.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetNicknameRequest {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(?![0-9]+$)[a-zA-Z0-9_](?:[a-zA-Z0-9_]*(?:\\.[a-zA-Z0-9_]+)?){1,19}$")
    private String nickname;
}
