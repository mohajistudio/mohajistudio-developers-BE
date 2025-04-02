package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDto {
    private UUID id;
    private String nickname;
    private String email;
    private String profileImageUrl;
    private UUID profileImageId;
    private String bio;
    private String jobRole;
    private Role role;
    private List<ContactDto> contacts;

    @QueryProjection
    public UserDetailsDto(UUID id, String nickname, String email, String profileImageUrl, UUID profileImageId, String bio, String jobRole, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImageId = profileImageId;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.jobRole = jobRole;
        this.role = role;
    }
}
