package com.mohajistudio.developers.database.repository.tag;

import com.mohajistudio.developers.database.dto.QTagDto;
import com.mohajistudio.developers.database.dto.TagDto;
import com.mohajistudio.developers.database.entity.Tag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.UUID;

import static com.mohajistudio.developers.database.entity.QPostTag.postTag;
import static com.mohajistudio.developers.database.entity.QTag.tag;


@RequiredArgsConstructor
public class TagCustomRepositoryImpl implements TagCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Tag findByTitle(String title) {
        return jpaQueryFactory
                .selectFrom(tag)
                .where(eqTitle(title))
                .fetchOne();
    }

    @Override
    public Page<TagDto> findAllTagDto(Pageable pageable) {
        List<TagDto> tags = jpaQueryFactory
                .select(new QTagDto(
                        tag.id,
                        tag.title,
                        tag.tagCount
                ))
                .from(tag)
                .orderBy(tag.tagCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(tag.count())
                .from(tag);

        return PageableExecutionUtils.getPage(tags, pageable, () -> totalCount.fetch().size());
    }

    @Override
    public Page<TagDto> findAllTagDtoByUserId(Pageable pageable, UUID userId) {
        List<TagDto> tags = jpaQueryFactory
                .select(new QTagDto(
                        tag.id,
                        tag.title,
                        postTag.tagId.count()
                ))
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .where(eqUserId(userId))
                .groupBy(tag.id, tag.title)
                .orderBy(postTag.tagId.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(tag.countDistinct())
                .from(postTag)
                .join(tag).on(postTag.tagId.eq(tag.id))
                .where(eqUserId(userId));


        return PageableExecutionUtils.getPage(tags, pageable, () -> totalCount.fetch().size());
    }

    private BooleanExpression eqTitle(String title) {
        if (StringUtils.isNullOrEmpty(title)) return null;
        return tag.title.eq(title);
    }

    private BooleanExpression eqUserId(UUID userId) {
        return postTag.userId.eq(userId);
    }
}
