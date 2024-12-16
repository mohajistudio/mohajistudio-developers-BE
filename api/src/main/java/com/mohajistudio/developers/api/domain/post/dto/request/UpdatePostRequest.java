package com.mohajistudio.developers.api.domain.post.dto.request;

import com.mohajistudio.developers.database.enums.PostStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePostRequest {
    private String title;
    private String summary;
    private String content;
    private PostStatus status;
}
