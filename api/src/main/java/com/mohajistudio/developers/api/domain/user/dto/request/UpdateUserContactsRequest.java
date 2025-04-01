package com.mohajistudio.developers.api.domain.user.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateUserContactsRequest {
    List<UpdateContactRequest> contacts;
}
