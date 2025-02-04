package com.mohajistudio.developers.api.domain.user;

import com.mohajistudio.developers.api.domain.user.service.UserService;
import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
