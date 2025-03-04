package com.mohajistudio.developers.database.repository.contacttype;

import com.mohajistudio.developers.database.dto.ContactTypeDto;
import com.mohajistudio.developers.database.dto.QContactTypeDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.mohajistudio.developers.database.entity.QContactType.contactType;

@RequiredArgsConstructor
public class ContactTypeCustomRepositoryImpl implements ContactTypeCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ContactTypeDto> findAllContactTypeDto() {
        return jpaQueryFactory
                .select(new QContactTypeDto(
                        contactType.id,
                        contactType.name,
                        contactType.imageUrl
                ))
                .from(contactType)
                .fetch();
    }
}
