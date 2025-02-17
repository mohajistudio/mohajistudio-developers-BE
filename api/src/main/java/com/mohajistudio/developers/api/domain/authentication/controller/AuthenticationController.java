package com.mohajistudio.developers.api.domain.authentication.controller;

import com.mohajistudio.developers.api.domain.authentication.dto.request.*;
import com.mohajistudio.developers.api.domain.authentication.dto.response.EmailVerifyResponse;
import com.mohajistudio.developers.api.domain.authentication.service.RegisterService;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.service.AuthenticationService;
import com.mohajistudio.developers.api.domain.authentication.service.EmailService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.mohajistudio.developers.database.enums.VerificationType;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public EmailVerifyResponse postResetPasswordRequest(@Valid @RequestBody EmailRequest emailRequest) {
        Optional<User> findUser = userRepository.findByEmail(emailRequest.getEmail());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        if(user.getRole() == Role.ROLE_UNREGISTERED) {
            throw new CustomException(ErrorCode.INCOMPLETE_REGISTRATION);
        }

        EmailVerification emailVerification = emailService.requestEmailVerification(emailRequest.getEmail(), VerificationType.PASSWORD_RESET);

        return EmailVerifyResponse.builder().expiredAt(emailVerification.getExpiredAt()).build();
    }

    @PostMapping("/password-reset/verify")
    @Transactional
    public GeneratedToken postResetPasswordVerify(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
        Optional<User> findUser = userRepository.findByEmail(emailVerifyRequest.getEmail());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        emailService.verifyEmailCode(emailVerifyRequest.getEmail(), emailVerifyRequest.getCode(), VerificationType.PASSWORD_RESET);

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

    @PostMapping("/delete-account/request")
    public EmailVerifyResponse postDeleteAccountRequest(@AuthenticationPrincipal CustomUserDetails userDetails) {
        EmailVerification emailVerification = emailService.requestEmailVerification(userDetails.getUsername(), VerificationType.ACCOUNT_DELETE);

        return EmailVerifyResponse.builder().expiredAt(emailVerification.getExpiredAt()).build();
    }

    @PostMapping("/delete-account/verify")
    @Transactional
    public void postDeleteAccountVerify(@AuthenticationPrincipal CustomUserDetails userDetails, @Valid @RequestBody DeleteAccountVerifyRequest deleteAccountVerifyRequest) {
        emailService.verifyEmailCode(userDetails.getUsername(), deleteAccountVerifyRequest.getCode(), VerificationType.ACCOUNT_DELETE);

        authenticationService.deleteAccount(userDetails.getUserId());
    }
}
