package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;

import java.util.List;

public interface EmailVerificationCustomRepository {
    List<EmailVerification> findAllRequestedToday(String email, VerificationType verificationType);

    EmailVerification findByEmail(String email, VerificationType verificationType);
}
