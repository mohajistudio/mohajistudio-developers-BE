package com.mohajistudio.developers.api.domain.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class DeleteAccountVerifyRequest {
    @NotNull
    @NotBlank
    @Length(min = 6, max = 6)
    private String code;
}
