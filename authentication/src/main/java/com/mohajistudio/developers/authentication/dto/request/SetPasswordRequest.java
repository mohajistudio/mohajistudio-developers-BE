package com.mohajistudio.developers.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetPasswordRequest {
    @NotNull
    @NotEmpty
    private String password;
}
