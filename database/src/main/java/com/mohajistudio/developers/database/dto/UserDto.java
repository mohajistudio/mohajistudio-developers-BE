package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private UUID id;
    private String nickname;
    private String email;
    private String password;
    private Role role;
    private String refreshToken;
}
