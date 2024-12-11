package com.mohajistudio.developers.authentication.service;

import ch.qos.logback.core.util.StringUtil;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;

    public void registerUser(String email) {
        User user = User.builder().email(email).role(Role.ROLE_GUEST).build();

        userRepository.save(user);
    }

    public boolean checkIsRegistered(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();

        if (StringUtil.isNullOrEmpty(user.getPassword())) {
            return false;
        } else if (StringUtil.isNullOrEmpty(user.getNickname())) {
            return false;
        }
        return true;
    }
}
