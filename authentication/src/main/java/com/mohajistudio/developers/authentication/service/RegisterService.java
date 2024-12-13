package com.mohajistudio.developers.authentication.service;

import ch.qos.logback.core.util.StringUtil;
import com.mohajistudio.developers.authentication.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RegisterService {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public GeneratedToken registerAndGenerateToken(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isPresent()) {
            User user = findUser.get();

            GeneratedToken generatedToken = jwtUtil.generateToken(user.getId(), user.getRole(), user.getEmail());
            user.setRefreshToken(generatedToken.getRefreshToken());
            userRepository.save(user);

            return generatedToken;
        }

        User user = User.builder().email(email).role(Role.ROLE_GUEST).build();

        GeneratedToken generatedToken = jwtUtil.generateToken(user.getId(), user.getRole(), user.getEmail());
        user.setRefreshToken(generatedToken.getRefreshToken());
        userRepository.save(user);

        return generatedToken;
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

        if (user.getPassword() != null) {
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

        if (user.getNickname() != null) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        user.setNickname(nickname);
        userRepository.save(user);
    }
}
