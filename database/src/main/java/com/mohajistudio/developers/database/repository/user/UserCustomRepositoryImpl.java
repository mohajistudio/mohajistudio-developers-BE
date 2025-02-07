package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.*;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.enums.Role;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mohajistudio.developers.database.entity.QContact.contact;
import static com.mohajistudio.developers.database.entity.QContactType.contactType;
import static com.mohajistudio.developers.database.entity.QUser.user;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<UserDto> findAllUserDto(Pageable pageable, Role role) {
        List<UserDto> users = jpaQueryFactory.select(new QUserDto(
                        user.id,
                        user.nickname,
                        user.email,
                        user.profileImageUrl,
                        user.role
                )).from(user)
                .where(eqRole(role))
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(user.count())
                .from(user);

        return PageableExecutionUtils.getPage(users, pageable, totalCount::fetchOne);
    }

    @Override
    public UserDetailsDto findUserDetailsDto(String nickname) {
        UserDetailsDto userDetailsDto = jpaQueryFactory
                .select(new QUserDetailsDto(
                        user.id,
                        user.nickname,
                        user.email,
                        user.profileImageUrl,
                        user.bio,
                        user.role
                ))
                .from(user)
                .where(user.nickname.eq(nickname))
                .fetchOne();

        if(userDetailsDto == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<ContactDto> contacts = jpaQueryFactory
                .select(new QContactDto(
                        contact.id,
                        contact.displayName,
                        contact.url,
                        contactType.name,
                        contactType.imageUrl
                ))
                .from(contact)
                .leftJoin(contactType).on(contact.contactTypeId.eq(contactType.id))
                .where(contact.userId.eq(Objects.requireNonNull(userDetailsDto).getId())).fetch();

        userDetailsDto.setContacts(contacts);

        return userDetailsDto;
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) return null;
        return user.email.eq(email);
    }

    private BooleanExpression eqPassword(String password) {
        if (StringUtils.isNullOrEmpty(password)) return null;
        return user.password.eq(password);
    }

    private BooleanExpression eqRole(Role role) {
        if (role == null) return null;
        return user.role.eq(role);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<User> pathBuilder = new PathBuilder<>(user.getType(), user.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(direction, pathBuilder.getString(order.getProperty())));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
