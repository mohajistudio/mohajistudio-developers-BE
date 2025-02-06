package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.Role;
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
    private String bio;
    private Role role;
    private List<ContactDto> contacts;

    // QueryDSL Projections에서 사용
    public UserDetailsDto(UUID id, String nickname, String email, String profileImageUrl, String bio, Role role) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.bio = bio;
        this.role = role;
    }
}
