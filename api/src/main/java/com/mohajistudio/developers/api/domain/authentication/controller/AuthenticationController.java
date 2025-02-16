package com.mohajistudio.developers.api.domain.authentication.controller;

import com.mohajistudio.developers.api.domain.authentication.dto.request.*;
import com.mohajistudio.developers.api.domain.authentication.service.RegisterService;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.service.AuthenticationService;
import com.mohajistudio.developers.api.domain.authentication.service.EmailService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.VerificationType;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final RegisterService registerService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public GeneratedToken postLogin(@Valid @RequestBody LoginRequest loginRequest) {
        registerService.checkUserRegistered(loginRequest.getEmail());

        return authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/logout")
    public void postLogout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authenticationService.logout(userDetails.getUserId());
        authenticationService.saveLogoutTime(userDetails.getUserId());
    }

    @PostMapping("/refresh")
    public GeneratedToken postRefreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return authenticationService.refreshToken(refreshRequest.getRefreshToken());
    }

    @PostMapping("/password-reset/request")
    public void postResetPasswordRequest(@Valid @RequestBody ResetPasswordRequest forgotPasswordRequest) {
        registerService.checkUserRegistered(forgotPasswordRequest.getEmail());

        emailService.requestEmailVerification(forgotPasswordRequest.getEmail(), VerificationType.PASSWORD_RESET);
    }

    @PostMapping("/password-reset/verify")
    @Transactional
    public GeneratedToken postResetPasswordVerify(@Valid @RequestBody ResetPasswordVerifyRequest resetPasswordVerifyRequest) {
        Optional<User> findUser = userRepository.findByEmail(resetPasswordVerifyRequest.getEmail());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        emailService.verifyEmailCode(resetPasswordVerifyRequest.getEmail(), resetPasswordVerifyRequest.getCode(), VerificationType.PASSWORD_RESET);

        authenticationService.saveLogoutTime(user.getId());

        authenticationService.resetPassword(user);

        return authenticationService.generateToken(user);
    }

    @PostMapping("/password-reset/update")
    public void postResetPasswordUpdate(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody ResetPasswordUpdateRequest resetPasswordUpdateRequest) {
        Optional<User> findUser = userRepository.findById(userDetails.getUserId());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(user.getPassword() != null) {
            throw new CustomException(ErrorCode.PASSWORD_ALREADY_SET);
        }

        authenticationService.updatePassword(user, resetPasswordUpdateRequest.getPassword());
    }

    @DeleteMapping("/{userId}")
    public void deleteAccount(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID userId) {
        if(!userId.equals(userDetails.getUserId())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        authenticationService.deleteAccount(userId);
    }
}
