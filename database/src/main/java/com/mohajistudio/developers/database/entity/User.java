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
@Table(name = "users")
public class User extends BaseEntity {
    @Column(unique = true, length = 20)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(length = 30)
    private String jobRole;

    /// MediaFile Id
    @Column
    private UUID profileImageId;

    @Column(length = 100)
    private String bio;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;
}
