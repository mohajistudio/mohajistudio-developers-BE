package com.mohajistudio.developers.authentication.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohajistudio.developers.authentication.config.JwtProperties;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import com.mohajistudio.developers.database.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private static final String LOGOUT_PREFIX = "logout:";

    public GeneratedToken login(String email, String password) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        boolean isMatches = passwordEncoder.matches(password, user.getPassword());

        if(!isMatches) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        return generateToken(user);
    }

    public void logout(UUID userId) {
        Optional<User> findUser = userRepository.findById(userId);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        user.setRefreshToken(null);

        userRepository.save(user);
    }

    public void saveLogoutTime(UUID userId) {
        long expireSeconds = jwtProperties.getAccessTokenPeriod();
        long logoutTime = Instant.now().getEpochSecond(); // 현재 UNIX 타임스탬프

        String key = LOGOUT_PREFIX + userId;
        redisUtil.setValue(key, String.valueOf(logoutTime), expireSeconds, TimeUnit.SECONDS);
    }

    public Long getLogoutTime(UUID userId) {
        String key = LOGOUT_PREFIX + userId;
        String logoutTime = redisUtil.getValue(key);
        return (logoutTime != null) ? Long.parseLong(logoutTime) : null;
    }

    public void resetPassword(User user) {
        if (user.getPassword() == null) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_SET);
        }

        user.setPassword(null);

        userRepository.save(user);
    }

    public void updatePassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    public GeneratedToken refreshToken(String refreshToken) {
        Map<String, Object> claims = jwtUtil.extractPayload(refreshToken);

        ObjectMapper objectMapper = new ObjectMapper();
        UserDto userDto = objectMapper.convertValue(claims.get("user"), UserDto.class);

        Optional<User> findUser = userRepository.findById(userDto.getId());

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(!user.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        return generateToken(user);
    }

    public void deleteAccount(UUID userId) {
        Optional<User> findUser = userRepository.findById(userId);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        userRepository.delete(user);

        //TODO Kafka를 통해 HostedService로 이벤트를 발생시켜 처리 후 탈퇴 메일 전송
    }

    public GeneratedToken generateToken(User user) {
        UserDto userDto = UserDto.builder().id(user.getId()).nickname(user.getNickname()).email(user.getEmail()).profileImageUrl(user.getProfileImageUrl()).role(user.getRole()).build();

        GeneratedToken generatedToken = jwtUtil.generateToken(userDto);

        user.setRefreshToken(generatedToken.getRefreshToken());
        userRepository.save(user);

        return generatedToken;
    }
}
