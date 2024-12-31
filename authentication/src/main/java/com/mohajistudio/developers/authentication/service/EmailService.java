package com.mohajistudio.developers.authentication.service;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.enums.VerificationType;
import com.mohajistudio.developers.database.repository.emailverification.EmailVerificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final EmailVerificationRepository emailVerificationRepository;

    @Transactional
    public void requestEmailVerification(String email, VerificationType verificationType) {
        List<EmailVerification> requestedTodayByEmail = emailVerificationRepository.findAllRequestedToday(email, verificationType);

        // 이미 24시간 동안 3번의 이메일 인증 요청을 보냈을 경우
        if(requestedTodayByEmail.size() >= 3) {
            throw new CustomException(ErrorCode.EMAIL_REQUEST_LIMIT_EXCEEDED);
        }

        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email, verificationType);

        // 만료되지 않은 이메일이 존재할 경우
        if (emailVerification != null) {
            LocalDateTime now = LocalDateTime.now();
            emailVerification.setExpiredAt(now);
            emailVerificationRepository.save(emailVerification);
        }

        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        Context context = new Context();
        context.setVariable("code", verificationCode);

        String htmlContent = templateEngine.process("verification-email-template", context);

        LocalDateTime expiredAt = LocalDateTime.now();
        expiredAt = expiredAt.plusMinutes(5);

        EmailVerification newEmailVerification = EmailVerification.builder().code(verificationCode).email(email).expiredAt(expiredAt).verificationType(verificationType).build();

        emailVerificationRepository.save(newEmailVerification);

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Your Verification Code");
            helper.setText(htmlContent, true);
            helper.setFrom("mohajistudio@gmail.com");

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new CustomException(ErrorCode.EMAIL_SEND_FAILURE, e.getMessage());
        }
    }

    @Transactional
    public void verifyEmailCode(String email, String code, VerificationType verificationType) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(email, verificationType);

        if (emailVerification == null) {
            throw new CustomException(ErrorCode.INVALID_EMAIL);
        }

        if (emailVerification.getAttempts() >= 5) {
            throw new CustomException(ErrorCode.EXCEEDED_VERIFICATION_ATTEMPTS);
        }

        LocalDateTime now = LocalDateTime.now();
        emailVerification.setAttempts(emailVerification.getAttempts() + 1);

        if (!emailVerification.getCode().equals(code)) {
            throw new CustomException(ErrorCode.INVALID_VERIFICATION_CODE);
        }

        emailVerification.setVerifiedAt(now);
        emailVerificationRepository.save(emailVerification);
    }
}
