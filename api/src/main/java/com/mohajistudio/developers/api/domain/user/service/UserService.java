package com.mohajistudio.developers.api.domain.user.service;

import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Page<UserDto> findAllUser(Pageable pageable, Role role) {
        return userRepository.findAllUserDto(pageable, role);
    }
}
