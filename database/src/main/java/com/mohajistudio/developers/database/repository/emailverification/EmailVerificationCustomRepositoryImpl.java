package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.dto.EmailVerificationDto;
import com.mohajistudio.developers.database.dto.QEmailVerificationDto;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mohajistudio.developers.database.entity.QEmailVerification.emailVerification;

@RequiredArgsConstructor
public class EmailVerificationCustomRepositoryImpl implements EmailVerificationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<EmailVerificationDto> findAllEmailVerificationDto(Pageable pageable) {
        List<EmailVerificationDto> emailVerifications = jpaQueryFactory.select(new QEmailVerificationDto(
                        emailVerification.id,
                        emailVerification.email,
                        emailVerification.code,
                        emailVerification.attempts,
                        emailVerification.verificationType,
                        emailVerification.verifiedAt,
                        emailVerification.expiredAt,
                        emailVerification.createdAt,
                        emailVerification.updatedAt
                )).from(emailVerification)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()).fetch();

        JPAQuery<Long> totalCount = jpaQueryFactory
                .select(emailVerification.count())
                .from(emailVerification);

        return PageableExecutionUtils.getPage(emailVerifications, pageable, totalCount::fetchOne);
    }
    @Override
    public List<EmailVerification> findAllRequestedToday(String email, VerificationType verificationType) {
        LocalDateTime now = LocalDateTime.now();

        return jpaQueryFactory
                .selectFrom(emailVerification)
                .where(eqEmail(email),
                        eqVerificationType(verificationType),
                        emailVerification.createdAt.between(now.minusHours(24), now)
                )
                .fetch();
    }

    public EmailVerification findByEmail(String email, VerificationType verificationType) {
        return jpaQueryFactory
                .selectFrom(emailVerification)
                .where(eqEmail(email),
                        emailVerification.verifiedAt.isNull(),
                        eqVerificationType(verificationType),
                        emailVerification.expiredAt.after(LocalDateTime.now()))
                .fetchOne();
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) return null;
        return emailVerification.email.eq(email);
    }

    private BooleanExpression eqVerificationType(VerificationType verificationType) {
        if (verificationType == null) return null;
        return emailVerification.verificationType.eq(verificationType);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : sort) {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            PathBuilder<EmailVerification> pathBuilder = new PathBuilder<>(emailVerification.getType(), emailVerification.getMetadata());
            orderSpecifiers.add(new OrderSpecifier<>(direction, pathBuilder.getString(order.getProperty())));
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
