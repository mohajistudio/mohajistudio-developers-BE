package com.mohajistudio.developers.authentication.controller;

import com.mohajistudio.developers.authentication.dto.request.EmailRequest;
import com.mohajistudio.developers.authentication.dto.request.EmailVerifyRequest;
import com.mohajistudio.developers.authentication.service.EmailService;
import com.mohajistudio.developers.authentication.service.RegisterService;
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
        emailService.sendVerificationEmail(emailRequest.getEmail());
    }

    @PostMapping("/email/verify")
    public void postEmailVerify(@Valid @RequestBody EmailVerifyRequest emailVerifyRequest) {
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
        return registerService.checkIsRegistered(emailRequest.getEmail());
    }
}
