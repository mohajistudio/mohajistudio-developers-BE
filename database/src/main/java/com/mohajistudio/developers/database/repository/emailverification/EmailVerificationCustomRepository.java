package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.dto.EmailVerificationDto;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmailVerificationCustomRepository {
    Page<EmailVerificationDto> customFindAll(Pageable pageable);

    List<EmailVerification> findAllRequestedToday(String email, VerificationType verificationType);

    EmailVerification findByEmail(String email, VerificationType verificationType);
}
