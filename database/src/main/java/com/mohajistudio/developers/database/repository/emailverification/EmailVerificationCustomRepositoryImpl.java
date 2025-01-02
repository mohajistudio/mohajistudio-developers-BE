package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.mohajistudio.developers.database.entity.QEmailVerification.emailVerification;

@RequiredArgsConstructor
public class EmailVerificationCustomRepositoryImpl implements EmailVerificationCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

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
}
