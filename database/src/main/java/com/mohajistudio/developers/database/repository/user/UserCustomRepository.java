package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<UserDto> findAllUserDto(Pageable pageable, Role role);
}
