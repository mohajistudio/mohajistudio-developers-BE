package com.mohajistudio.developers.admin.controller;

import com.mohajistudio.developers.common.enums.ErrorCode;
import com.mohajistudio.developers.common.exception.CustomException;
import com.mohajistudio.developers.database.dto.EmailVerificationDto;
import com.mohajistudio.developers.database.entity.EmailVerification;
import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.emailverification.EmailVerificationRepository;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/email-verifications")
@RequiredArgsConstructor
public class EmailVerificationController {
    final EmailVerificationRepository emailVerificationRepository;
    final UserRepository userRepository;

    @GetMapping
    public String getEmailVerificationsPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        Page<EmailVerificationDto> emailVerifications = emailVerificationRepository.findAllEmailVerificationDto(pageable);

        List<Sort.Order> sort = new ArrayList<>();
        pageable.getSort().stream().forEach(sort::add);

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("emailVerifications", emailVerifications);
        model.addAttribute("sort", sort);

        return "pages/email-verifications";
    }

    @GetMapping("/{emailVerificationId}/edit")
    public String getEditEmailVerificationPage(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID emailVerificationId,
            Model model
    ) {
        Optional<User> findUser = userRepository.findByEmail(userDetails.getUsername());

        if (findUser.isEmpty()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        User user = findUser.get();

        Optional<EmailVerification> findEmailVerification = emailVerificationRepository.findById(emailVerificationId);

        if (findEmailVerification.isEmpty()) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        EmailVerification emailVerification = findEmailVerification.get();

        model.addAttribute("nickname", user.getNickname());
        model.addAttribute("emailVerification", emailVerification);

        return "pages/edit-email-verification";
    }

    @PostMapping("/{emailVerificationId}/edit")
    public String postUserDetails(
            @PathVariable UUID emailVerificationId, @ModelAttribute EmailVerification editEmailVerification
    ) {
        Optional<EmailVerification> findEmailVerification = emailVerificationRepository.findById(emailVerificationId);

        if (findEmailVerification.isEmpty()) {
            throw new CustomException(ErrorCode.ENTITY_NOT_FOUND);
        }

        EmailVerification emailVerification = findEmailVerification.get();

        emailVerification.setAttempts(editEmailVerification.getAttempts());
        emailVerification.setVerificationType(editEmailVerification.getVerificationType());
        emailVerification.setExpiredAt(editEmailVerification.getExpiredAt());

        emailVerificationRepository.save(emailVerification);

        return "redirect:/email-verifications";
    }

    @DeleteMapping("/{emailVerificationId}")
    public String deleteUser(@PathVariable UUID emailVerificationId) {
        emailVerificationRepository.deleteById(emailVerificationId);

        return "redirect:/email-verifications";
    }
}
