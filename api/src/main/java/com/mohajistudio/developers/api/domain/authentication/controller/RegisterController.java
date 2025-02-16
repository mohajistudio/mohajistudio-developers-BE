package com.mohajistudio.developers.api.domain.authentication.controller;

import com.mohajistudio.developers.api.domain.authentication.dto.response.EmailVerifyResponse;
import com.mohajistudio.developers.common.dto.GeneratedToken;
import com.mohajistudio.developers.api.domain.authentication.dto.request.EmailRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.EmailVerifyRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.SetNicknameRequest;
import com.mohajistudio.developers.api.domain.authentication.dto.request.SetPasswordRequest;
import com.mohajistudio.developers.api.domain.authentication.service.EmailService;
import com.mohajistudio.developers.api.domain.authentication.service.RegisterService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;
    private final EmailService emailService;

    @PostMapping("/email/request")
    public EmailVerifyResponse requestEmailVerification(@Valid @RequestBody EmailRequest emailRequest) {
        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(emailRequest.getEmail());

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        EmailVerification emailVerification = emailService.requestEmailVerification(emailRequest.getEmail(), VerificationType.EMAIL_VERIFICATION);

        return EmailVerifyResponse.builder().expiredAt(emailVerification.getExpiredAt()).build();
    }

    @PostMapping("/email/verify")
    public GeneratedToken verifyEmailCode(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(emailVerifyRequest.getEmail());

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        emailService.verifyEmailCode(emailVerifyRequest.getEmail(), emailVerifyRequest.getCode(), VerificationType.EMAIL_VERIFICATION);

        return registerService.registerAndGenerateToken(emailVerifyRequest.getEmail());
    }

    @PostMapping("/password")
    public void setPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SetPasswordRequest setPasswordRequest) {
        String email = userDetails.getUsername();

        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(email);

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        registerService.setPassword(email, setPasswordRequest.getPassword());
    }

    @PostMapping("/nickname")
    public void setNickname(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SetNicknameRequest setNicknameRequest) {
        String email = userDetails.getUsername();

        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(email);

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        registerService.setNickname(email, setNicknameRequest.getNickname());
    }

    @GetMapping("/status")
    public void checkRegistrationStatus(@Valid @RequestParam String email) {
        registerService.checkUserRegistered(email);
    }
}
