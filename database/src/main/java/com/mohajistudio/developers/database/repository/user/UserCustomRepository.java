package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.dto.UserDetailsDto;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserCustomRepository {
    Page<UserDto> findAllUserDto(Pageable pageable, Role role);

    UserDetailsDto findUserDetailsDto(String nickname);

    UserDetailsDto findUserDetailsDto(UUID userId);
}
