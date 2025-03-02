package com.mohajistudio.developers.database.repository.contacttype;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContactTypeCustomRepositoryImpl implements ContactTypeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;
}
