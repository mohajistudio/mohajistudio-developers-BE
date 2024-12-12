package com.mohajistudio.developers.authentication.service;

import ch.qos.logback.core.util.StringUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findOrCreateUser(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isPresent()) {
            return findUser.get();
        }

        User user = User.builder().email(email).role(Role.ROLE_GUEST).build();

        return userRepository.save(user);
    }

    public boolean isUserRegistered(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();
        if (StringUtil.isNullOrEmpty(user.getPassword())) {
            return false;
        } else if (StringUtil.isNullOrEmpty(user.getNickname())) {
            return false;
        }
        return true;
    }

    public boolean isUserRegistrationComplete(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        // 유저가 아닌 경우 false
        if (findUser.isEmpty()) {
            return false;
        }

        User user = findUser.get();

        // 닉네임 또는 비밀번호 설정을 하지 않은 경우 false
        return user.getNickname() != null && user.getPassword() != null;
    }

    public void setPassword(String email, String password) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(user.getPassword() != null) {
            throw new CustomException(ErrorCode.PASSWORD_ALREADY_SET);
        }

        String encodedPassword = passwordEncoder.encode(password);
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    public void setNickname(String email, String nickname) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(user.getNickname() != null) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        user.setNickname(nickname);
        userRepository.save(user);
    }
}
