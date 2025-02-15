package com.mohajistudio.developers.api.domain.user;

import com.mohajistudio.developers.api.domain.user.dto.request.UpdateUserRequest;
import com.mohajistudio.developers.authentication.dto.CustomUserDetails;
import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.UserDetailsDto;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.enums.Role;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    CustomPageResponse<UserDto> getUsers(Pageable pageable, @RequestParam(required = false) Role role) {
        Page<UserDto> users = userService.findAllUser(pageable, role);

        return new CustomPageResponse<>(users);
    }

    @GetMapping("/{nickname}")
    UserDetailsDto getUserDetails(@PathVariable String nickname) {
        return userService.findUserDetails(nickname);
    }

    @PatchMapping("/{userId}")
    @Transactional
    void patchUser(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable UUID userId, @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        if(!userDetails.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        userService.updateUser(userId, updateUserRequest);
    }
}
