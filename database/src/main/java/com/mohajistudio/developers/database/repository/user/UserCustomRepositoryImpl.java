package com.mohajistudio.developers.database.repository.user;

import com.mohajistudio.developers.database.dto.UserDto;
import com.mohajistudio.developers.database.entity.User;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
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

import static com.mohajistudio.developers.database.entity.QUser.user;

@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public Page<UserDto> customFindAll(Pageable pageable) {
        List<UserDto> users = jpaQueryFactory.select(Projections.constructor(UserDto.class,
                        user.id,
                        user.nickname,
                        user.email,
                        user.password,
                        user.role,
                        user.refreshToken,
                        user.createdAt,
                        user.updatedAt
                )).from(user)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(user.count())
                .from(user);

        return PageableExecutionUtils.getPage(users, pageable, totalCount::fetchOne);
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) return null;
        return user.email.eq(email);
    }

    private BooleanExpression eqPassword(String password) {
        if (StringUtils.isNullOrEmpty(password)) return null;
        return user.password.eq(password);
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
