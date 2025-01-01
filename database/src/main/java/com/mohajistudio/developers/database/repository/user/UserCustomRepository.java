package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<UserDto> customFindALl(Pageable pageable);
    User findByEmailAndPassword(String email, String password);
}
