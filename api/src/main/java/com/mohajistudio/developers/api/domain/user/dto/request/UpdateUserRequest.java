package com.mohajistudio.developers.api.domain.user.dto.request;

import com.mohajistudio.developers.database.entity.Contact;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserRequest {
    private String nickname;
    private String jobRole;
    private String bio;
    private List<Contact> contact;
}
