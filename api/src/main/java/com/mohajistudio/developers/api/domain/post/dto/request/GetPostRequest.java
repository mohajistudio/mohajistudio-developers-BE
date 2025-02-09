package com.mohajistudio.developers.api.domain.post.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class GetPostRequest {
    private UUID userId;
    private String search;
    private List<String> tags;
}
