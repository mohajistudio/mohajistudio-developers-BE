package com.mohajistudio.developers.database.repository.post;

import com.mohajistudio.developers.database.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostCustomRepository {
    Page<PostDto> findAllPost(Pageable pageable);
}
