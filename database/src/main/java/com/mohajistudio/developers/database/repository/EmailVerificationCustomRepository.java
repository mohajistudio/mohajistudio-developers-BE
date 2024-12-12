package com.mohajistudio.developers.database.repository;

import com.mohajistudio.developers.database.entity.EmailVerification;

public interface EmailVerificationCustomRepository {
    EmailVerification findByEmail(String email);
}
