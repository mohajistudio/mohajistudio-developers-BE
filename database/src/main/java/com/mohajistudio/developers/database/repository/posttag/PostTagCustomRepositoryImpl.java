package com.mohajistudio.developers.database.repository.posttag;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PostTagCustomRepositoryImpl implements PostTagCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

}
