package com.mohajistudio.developers.api.domain.post.dto.request;

import com.mohajistudio.developers.database.enums.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateAndUpdatePostRequest {

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String title;

    @NotNull
    @NotBlank
    private String content;

    @Size(max = 200)
    private String summary;

    private UUID thumbnailId;

    @NotNull
    private PostStatus status;

    private List<String> tags;
}
