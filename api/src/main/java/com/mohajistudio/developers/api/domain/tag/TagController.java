package com.mohajistudio.developers.api.domain.tag;

import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.database.dto.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    CustomPageResponse<TagDto> getTags(Pageable pageable) {
        Page<TagDto> tags = tagService.findAllTags(pageable);

        return new CustomPageResponse<>(tags);
    }
}
