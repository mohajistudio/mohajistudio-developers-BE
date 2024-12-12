package com.mohajistudio.developers.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class EmailVerifyRequest {
    @Email
    private String email;

    @NotNull
    @NotEmpty
    @Length(min = 6, max = 6)
    private String code;
}
