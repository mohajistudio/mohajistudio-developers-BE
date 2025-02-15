package com.mohajistudio.developers.database.repository.posttag;

import com.mohajistudio.developers.database.entity.PostTag;

import java.util.UUID;

public interface PostTagCustomRepository {
    PostTag findByTagIdAndUserIdAndPostId(UUID tagId, UUID userId, UUID postId);

    void deleteByTagIdAndPostId(UUID tagId, UUID postId);
}
