package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_nickname", columnList = "nickname", unique = true),
        @Index(name = "idx_user_email", columnList = "email", unique = true),
})
public class User extends BaseEntity {
    @Column(unique = true, length = 20)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    /// MediaFile Id
    @Column
    private UUID profileImageId;

    @Column
    private String profileImageUrl;

    @Column(length = 30)
    private String jobRole;

    @Column(length = 100)
    private String bio;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;
}
