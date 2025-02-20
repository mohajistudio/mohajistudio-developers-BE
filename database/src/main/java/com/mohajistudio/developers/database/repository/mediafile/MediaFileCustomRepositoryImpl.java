package com.mohajistudio.developers.database.repository.mediafile;

import com.mohajistudio.developers.database.entity.MediaFile;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

import static com.mohajistudio.developers.database.entity.QMediaFile.mediaFile;
import static com.mohajistudio.developers.database.entity.QPost.post;

@RequiredArgsConstructor
public class MediaFileCustomRepositoryImpl implements MediaFileCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public MediaFile findByIdAndUserId(UUID mediaFileId, UUID userId) {
        return jpaQueryFactory
                .selectFrom(mediaFile)
                .where(eqId(mediaFileId),
                        eqUserId(userId))
                .fetchOne();
    }

    @Override
    public Page<MediaFile> findAllByUserId(Pageable pageable, UUID userId) {
        List<MediaFile> mediaFiles = jpaQueryFactory.selectFrom(mediaFile)
                .where(
                        eqUserId(userId)
                )
                .orderBy(post.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(mediaFile.count())
                .from(mediaFile)
                .where(
                        eqUserId(userId)
                );

        return PageableExecutionUtils.getPage(mediaFiles, pageable, () -> totalCount.fetch().size());
    }

    private BooleanExpression eqId(UUID mediaFileId) {
        if (mediaFileId == null) return null;
        return mediaFile.id.eq(mediaFileId);
    }

    private BooleanExpression eqUserId(UUID userId) {
        if (userId == null) return null;
        return mediaFile.userId.eq(userId);
    }
}
