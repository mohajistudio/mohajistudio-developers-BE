package com.mohajistudio.developers.database.repository.post;

import com.mohajistudio.developers.database.dto.PostDetailsDto;
import com.mohajistudio.developers.database.dto.PostDto;
import com.mohajistudio.developers.database.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface PostCustomRepository {
    Page<PostDto> findAllPostDto(Pageable pageable, PostStatus status);

    PostDetailsDto findByIdPostDetailsDto(UUID id);
}
