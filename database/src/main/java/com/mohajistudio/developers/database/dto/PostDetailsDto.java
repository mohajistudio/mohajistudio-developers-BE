package com.mohajistudio.developers.database.dto;

import com.mohajistudio.developers.database.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailsDto {
    private UUID id;
    private UserDto user;
    private String title;
    private String content;
    private String summary;
    private String thumbnail;
    private PostStatus status;
    private LocalDateTime publishedAt;
    private Set<TagDto> tags;
}