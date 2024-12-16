package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.entity.EmailVerification;

import java.util.List;

public interface EmailVerificationCustomRepository {
    List<EmailVerification> findAllRequestedTodayByEmail(String email);

    EmailVerification findByEmail(String email);
}
