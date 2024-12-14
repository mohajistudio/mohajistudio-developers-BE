package com.mohajistudio.developers.authentication.controller;

import com.mohajistudio.developers.authentication.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.dto.request.EmailRequest;
import com.mohajistudio.developers.authentication.dto.request.EmailVerifyRequest;
import com.mohajistudio.developers.authentication.dto.request.SetNicknameRequest;
import com.mohajistudio.developers.authentication.dto.request.SetPasswordRequest;
import com.mohajistudio.developers.authentication.service.EmailService;
import com.mohajistudio.developers.authentication.service.RegisterService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
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
    public void postEmailRequest(@Valid @RequestBody EmailRequest emailRequest) {
        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(emailRequest.getEmail());

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        emailService.sendVerificationEmail(emailRequest.getEmail());
    }

    @PostMapping("/email/verify")
    public GeneratedToken postEmailVerify(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(emailVerifyRequest.getEmail());

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        emailService.verifyEmailCode(emailVerifyRequest.getEmail(), emailVerifyRequest.getCode());

        return registerService.registerAndGenerateToken(emailVerifyRequest.getEmail());
    }

    @PostMapping("/password")
    public void postPassword(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SetPasswordRequest setPasswordRequest) {
        String email = userDetails.getUsername();

        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(email);

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        registerService.setPassword(email, setPasswordRequest.getPassword());
    }

    @PostMapping("/nickname")
    public void postNickname(@AuthenticationPrincipal UserDetails userDetails, @Valid @RequestBody SetNicknameRequest setNicknameRequest) {
        String email = userDetails.getUsername();

        boolean isUserRegistrationComplete = registerService.isUserRegistrationComplete(email);

        if(isUserRegistrationComplete) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        registerService.setNickname(email, setNicknameRequest.getNickname());
    }

    @GetMapping("/status")
    public void getRegistrationStatus(@Valid @RequestParam EmailRequest emailRequest) {
        registerService.checkUserRegistered(emailRequest.getEmail());
    }
}
