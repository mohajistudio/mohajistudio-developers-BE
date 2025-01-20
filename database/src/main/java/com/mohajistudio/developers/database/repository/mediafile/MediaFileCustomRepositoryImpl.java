package com.mohajistudio.developers.database.repository.mediafile;

import com.mohajistudio.developers.database.entity.MediaFile;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.mohajistudio.developers.database.entity.QMediaFile.mediaFile;

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

    private BooleanExpression eqId(UUID mediaFileId) {
        if (mediaFileId == null) return null;
        return mediaFile.id.eq(mediaFileId);
    }

    private BooleanExpression eqUserId(UUID userId) {
        if (userId == null) return null;
        return mediaFile.userId.eq(userId);
    }
}
