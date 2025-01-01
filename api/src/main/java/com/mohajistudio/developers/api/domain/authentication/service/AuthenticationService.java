package com.mohajistudio.developers.api.domain.authentication.service;

import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.util.JwtUtil;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RegisterService registerService;

    public GeneratedToken login(String email, String password) {
        registerService.checkUserRegistered(email);

        String encodedPassword = passwordEncoder.encode(password);

        User user = userRepository.findByEmailAndPassword(email, encodedPassword);

        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        GeneratedToken generatedToken = jwtUtil.generateToken(user.getId(), user.getRole(), user.getEmail());

        user.setRefreshToken(generatedToken.getRefreshToken());
        userRepository.save(user);

        return generatedToken;
    }

    public GeneratedToken refreshToken(String refreshToken) {
        Map<String, Object> claims = jwtUtil.extractPayload(refreshToken);
        String email = (String) claims.get("email");

        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(user.getRefreshToken().equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        GeneratedToken generatedToken = jwtUtil.generateToken(user.getId(), user.getRole(), user.getEmail());
        user.setRefreshToken(generatedToken.getRefreshToken());
        userRepository.save(user);

        return generatedToken;
    }

    public void deleteAccount(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        userRepository.delete(user);

        //TODO Kafka를 통해 HostedService로 이벤트를 발생시켜 처리 후 탈퇴 메일 전송
    }
}
