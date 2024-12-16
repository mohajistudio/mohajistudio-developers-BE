package com.mohajistudio.developers.database.repository.emailverification;

import com.mohajistudio.developers.database.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, UUID>, EmailVerificationCustomRepository {
}
