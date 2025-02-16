package com.mohajistudio.developers.api.domain.authentication.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordUpdateRequest {
    @NotNull
    @NotEmpty
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$")
    private String password;
}
