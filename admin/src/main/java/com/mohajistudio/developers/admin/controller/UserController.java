package com.mohajistudio.developers.admin.controller;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    final UserRepository userRepository;

    @GetMapping
    public String getUsersPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        Page<UserDto> users = userRepository.customFindALl(pageable);

        List<Sort.Order> sort = new ArrayList<>();
        pageable.getSort().stream().forEach(sort::add);

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("users", users);
        model.addAttribute("sort", sort);

        return "/pages/users";
    }

    @GetMapping("/{userId}/edit")
    public String getUserDetailsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID userId,
            Model model
    ) {
        Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        Optional<User> findUserDetails = userRepository.findById(userId);

        if (findUserDetails.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User userDetail = findUserDetails.get();

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("user", userDetail);

        return "/pages/edit-user";
    }

    @PostMapping("/{userId}/edit")
    public String postUserDetails(
            @PathVariable UUID userId, @ModelAttribute User editUser
    ) {
        Optional<User> findUser = userRepository.findById(userId);

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        user.setNickname(editUser.getNickname());
        user.setEmail(editUser.getEmail());
        user.setRole(editUser.getRole());

        userRepository.save(user);

        return "redirect:/users";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable UUID userId) {
        userRepository.deleteById(userId);

        return "redirect:/users";
    }
}
