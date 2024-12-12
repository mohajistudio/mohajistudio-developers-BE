package com.mohajistudio.developers.database.repository;

import com.mohajistudio.developers.database.entity.EmailVerification;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohajistudio.developers.database.entity.QEmailVerification.emailVerification;

@Repository
@RequiredArgsConstructor
public class EmailVerificationCustomRepositoryImpl implements EmailVerificationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<EmailVerification> findAllRequestedTodayByEmail(String email) {
        LocalDateTime now = LocalDateTime.now();

        return jpaQueryFactory.selectFrom(emailVerification).where(emailVerification.email.eq(email),
                emailVerification.createdAt.between(now.minusHours(24), now)).fetch();
    }

    public EmailVerification findByEmail(String email) {
        return jpaQueryFactory.selectFrom(emailVerification).where(eqEmail(email), emailVerification.expiredAt.after(LocalDateTime.now())).fetchOne();
    }

    private BooleanExpression eqEmail(String email) {
        if (StringUtils.isNullOrEmpty(email)) return null;
        return emailVerification.email.eq(email);
    }
}
