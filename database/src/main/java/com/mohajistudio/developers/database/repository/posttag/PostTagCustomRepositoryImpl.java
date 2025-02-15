package com.mohajistudio.developers.database.repository.posttag;

import com.mohajistudio.developers.database.entity.PostTag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.mohajistudio.developers.database.entity.QPostTag.postTag;

@RequiredArgsConstructor
public class PostTagCustomRepositoryImpl implements PostTagCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public PostTag findByTagIdAndUserIdAndPostId(UUID tagId, UUID userId, UUID postId) {
        return jpaQueryFactory.selectFrom(postTag).where(eqPostId(postId), eqUserId(userId), eqTagId(tagId)).fetchOne();
    }

    @Override
    public void deleteByTagIdAndPostId(UUID tagId, UUID postId) {
        jpaQueryFactory.delete(postTag).where(eqTagId(tagId), eqPostId(postId)).execute();
    }

    BooleanExpression eqUserId(UUID userId) {
        if (userId == null) return null;
        return postTag.userId.eq(userId);
    }

    BooleanExpression eqPostId(UUID postId) {
        if (postId == null) return null;
        return postTag.postId.eq(postId);
    }

    BooleanExpression eqTagId(UUID tagId) {
        if (tagId == null) return null;
        return postTag.tagId.eq(tagId);
    }
}
