package com.mohajistudio.developers.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "email_verifications")
public class EmailVerification extends BaseEntity {
    @Column(nullable = false)
    private String email;

    @Column(nullable = false, length = 6)
    private String code;

    @Column(nullable = false, columnDefinition = "SMALLINT DEFAULT 0")
    private int attempts;

    @Column
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime expiredAt;
}
