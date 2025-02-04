package com.mohajistudio.developers.database.repository.tag;

import com.mohajistudio.developers.database.dto.TagDto;
import com.mohajistudio.developers.database.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagCustomRepository {
    Tag findByTitle(String title);

    Page<TagDto> findAllTagDto(Pageable pageable);
}