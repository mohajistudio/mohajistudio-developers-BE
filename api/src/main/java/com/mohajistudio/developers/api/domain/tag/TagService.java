package com.mohajistudio.developers.api.domain.tag;

import com.mohajistudio.developers.database.dto.TagDto;
import com.mohajistudio.developers.database.entity.PostTag;
import com.mohajistudio.developers.database.entity.Tag;
import com.mohajistudio.developers.database.repository.posttag.PostTagRepository;
import com.mohajistudio.developers.database.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;

    public Page<TagDto> findAllTags(Pageable pageable, UUID userId) {
        if(userId == null) {
            return tagRepository.findAllTagDto(pageable);
        }
        return tagRepository.findAllTagDtoByUserId(pageable, userId);
    }

    public void addTag(String tag, UUID userId, UUID postId) {
        Tag findTag = tagRepository.findByTitle(tag);

        if (findTag == null) {
            Tag newTag = Tag.builder().title(tag).userId(userId).tagCount(1).build();
            Tag savedTag = tagRepository.save(newTag);

            PostTag postTag = PostTag.builder().postId(postId).tagId(savedTag.getId()).userId(userId).build();
            postTagRepository.save(postTag);
        } else {
            findTag.setTagCount(findTag.getTagCount() + 1);
            Tag savedTag = tagRepository.save(findTag);

            PostTag postTag = PostTag.builder().postId(postId).tagId(savedTag.getId()).userId(userId).build();
            postTagRepository.save(postTag);
        }
    }
}
