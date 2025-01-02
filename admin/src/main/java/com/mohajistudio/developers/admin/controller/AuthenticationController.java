package com.mohajistudio.developers.admin.controller;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {
    final UserRepository userRepository;

    @GetMapping("/")
    public String getMainPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

        if(findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        model.addAttribute("nickname", user.getNickname());

        return "pages/index";
    }

    @GetMapping("/login")
    public String getLoginPage(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails != null) {
            return "redirect:/";
        }

        return "pages/login";
    }
}
