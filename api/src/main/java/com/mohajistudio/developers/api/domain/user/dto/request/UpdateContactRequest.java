package com.mohajistudio.developers.api.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateContactRequest {
    @NotNull
    private UUID contactTypeId;

    @NotNull
    @NotEmpty
    private String displayName;

    @NotNull
    @NotEmpty
    private String url;
}
