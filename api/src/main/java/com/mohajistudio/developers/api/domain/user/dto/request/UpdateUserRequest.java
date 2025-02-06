package com.mohajistudio.developers.api.domain.user.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRequest {
    @NotEmpty
    @Pattern(regexp = "^[a-z0-9_.](?:[a-z0-9]*[_.]?){1,19}$")
    private String nickname;
    private String jobRole;
    private String bio;
    private List<UpdateContactRequest> contact;
}
