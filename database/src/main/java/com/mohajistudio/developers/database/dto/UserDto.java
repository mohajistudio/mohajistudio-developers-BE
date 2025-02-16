package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private String jobRole;
    private Role role;

    @QueryProjection
    public UserDto(UUID id, String nickname, String email, String profileImageUrl, String jobRole, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }
}
