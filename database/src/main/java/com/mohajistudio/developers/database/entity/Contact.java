package com.mohajistudio.developers.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contacts")
public class Contact extends BaseEntity {
    @Column
    private UUID userId;

    @Column
    private UUID contactTypeId;

    @Column
    private String displayName;

    @Column
    private String url;
}