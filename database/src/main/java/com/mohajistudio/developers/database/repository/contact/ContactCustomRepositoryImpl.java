package com.mohajistudio.developers.database.repository.contact;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ContactCustomRepositoryImpl implements ContactCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

}
