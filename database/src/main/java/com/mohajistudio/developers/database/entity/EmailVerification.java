package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.VerificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_verifications", indexes = {
        @Index(name = "idx_email_verification_email", columnList = "email"),
})
public class EmailVerification extends BaseEntity {
    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "SMALLINT")
    private Short attempts;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private VerificationType verificationType;

    @Column
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;
}
