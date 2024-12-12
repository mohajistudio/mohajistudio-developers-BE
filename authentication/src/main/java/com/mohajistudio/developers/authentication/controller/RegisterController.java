package com.mohajistudio.developers.authentication.controller;

import com.mohajistudio.developers.authentication.dto.request.EmailRequest;
import com.mohajistudio.developers.authentication.dto.request.EmailVerifyRequest;
import com.mohajistudio.developers.authentication.service.EmailService;
import com.mohajistudio.developers.authentication.service.RegisterService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final RegisterService registerService;
    private final EmailService emailService;

    @PostMapping("/email/request")
    public void postEmailRequest(@Valid @RequestBody EmailRequest emailRequest) {
        boolean isUserExist = registerService.isUserExist(emailRequest.getEmail());

        if (isUserExist) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        emailService.sendVerificationEmail(emailRequest.getEmail());
    }

    @PostMapping("/email/verify")
    public void postEmailVerify(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
        boolean isUserExist = registerService.isUserExist(emailVerifyRequest.getEmail());

        if (isUserExist) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        emailService.verifyEmailCode(emailVerifyRequest.getEmail(), emailVerifyRequest.getCode());
        registerService.registerUser(emailVerifyRequest.getEmail());
    }

    @PostMapping("/password")
    public void postPassword() {

    }

    @PostMapping("/nickname")
    public void postNickname() {
    }

    @GetMapping("/status")
    public boolean getRegistrationStatus(@Valid @RequestBody EmailRequest emailRequest) {
        return registerService.isUserRegistered(emailRequest.getEmail());
    }
}
