package com.mohajistudio.developers.authentication.controller;

import com.mohajistudio.developers.authentication.dto.GeneratedToken;
import com.mohajistudio.developers.authentication.dto.request.LoginRequest;
import com.mohajistudio.developers.authentication.dto.request.RefreshRequest;
import com.mohajistudio.developers.authentication.service.AuthenticationService;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public GeneratedToken postLogin(@Valid @RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());
    }

    @PostMapping("/refresh")
    public GeneratedToken postRefreshToken(@Valid @RequestBody RefreshRequest refreshRequest) {
        return authenticationService.refreshToken(refreshRequest.getRefreshToken());
    }

    @DeleteMapping("/{email}")
    public void deleteAccount(@AuthenticationPrincipal UserDetails userDetails, @PathVariable String email) {
        if(!email.equals(userDetails.getUsername())) {
            throw new CustomException(ErrorCode.INVALID_INPUT_VALUE);
        }

        authenticationService.deleteAccount(email);
    }
}
