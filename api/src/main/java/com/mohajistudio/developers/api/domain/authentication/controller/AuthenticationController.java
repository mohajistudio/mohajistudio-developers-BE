package com.mohajistudio.developers.api.domain.authentication.controller;

import com.mohajistudio.developers.api.domain.authentication.service.RegisterService;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.api.domain.authentication.dto.request.ForgotPasswordRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.ForgotPasswordVerifyRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.LoginRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.RefreshRequest;
import com.mohajistudio.developers.authentication.service.AuthenticationService;
import com.mohajistudio.developers.api.domain.authentication.service.EmailService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.enums.VerificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final RegisterService registerService;
    private final AuthenticationService authenticationService;
    private final EmailService emailService;

    @PostMapping("/login")
    public GeneratedToken postLogin(@Valid @RequestBody LoginRequest loginRequest) {
        registerService.checkUserRegistered(loginRequest.getEmail());

        return authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/logout")
    public void logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        authenticationService.logout(userDetails.getUserId());
        authenticationService.saveLogoutTime(userDetails.getUserId());
    }

    @PostMapping("/refresh")
    public GeneratedToken postRefreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return authenticationService.refreshToken(refreshRequest.getRefreshToken());
    }

    @PostMapping("/forgot-password/request")
    public void postForgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
        emailService.requestEmailVerification(forgotPasswordRequest.getEmail(), VerificationType.PASSWORD_RESET);
    }

    @PostMapping("/forgot-password/verify")
    public void postForgotPasswordVerify(@Valid @RequestBody ForgotPasswordVerifyRequest forgotPasswordVerifyRequest) {
        emailService.verifyEmailCode(forgotPasswordVerifyRequest.getEmail(), forgotPasswordVerifyRequest.getCode(), VerificationType.PASSWORD_RESET);
    }

    @DeleteMapping("/{email}")
    public void deleteAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        if(!email.equals(userDetails.getUsername())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        authenticationService.deleteAccount(email);
    }
}
