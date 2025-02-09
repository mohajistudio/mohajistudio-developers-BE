package com.mohajistudio.developers.api.domain.tag;

import com.mohajistudio.developers.common.dto.response.CustomPageResponse;
import com.mohajistudio.developers.database.dto.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping
    CustomPageResponse<TagDto> getTags(Pageable pageable, @RequestParam(required = false) UUID userId) {
        Page<TagDto> tags = tagService.findAllTags(pageable, userId);

        return new CustomPageResponse<>(tags);
    }
}
