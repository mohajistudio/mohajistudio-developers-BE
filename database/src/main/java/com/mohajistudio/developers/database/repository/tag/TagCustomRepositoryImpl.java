package com.mohajistudio.developers.database.repository.tag;

import com.mohajistudio.developers.database.entity.Tag;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

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

    private BooleanExpression eqTitle(String title) {
        if (StringUtils.isNullOrEmpty(title)) return null;
        return tag.title.eq(title);
    }
}
