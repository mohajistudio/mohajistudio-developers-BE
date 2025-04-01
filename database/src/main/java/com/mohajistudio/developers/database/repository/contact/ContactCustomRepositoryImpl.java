package com.mohajistudio.developers.database.repository.contact;

import com.mohajistudio.developers.database.entity.Contact;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import static com.mohajistudio.developers.database.entity.QContact.contact;

@RequiredArgsConstructor
public class ContactCustomRepositoryImpl implements ContactCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Contact> findByUserId(UUID userId) {
        return jpaQueryFactory.selectFrom(contact)
                .where(eqUserId(userId))
                .fetch();
    }

    BooleanExpression eqUserId(UUID userId) {
        if (userId == null) return null;
        return contact.userId.eq(userId);
    }
}
