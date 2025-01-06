package com.mohajistudio.developers.api.domain.user;

import com.mohajistudio.developers.api.domain.user.dto.request.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public void patchUser(UpdateUserRequest updateUserRequest) {

    }
}
