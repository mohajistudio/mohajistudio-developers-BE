package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.ContactType;
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

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    @Column(length = 20)
    private String contactValue;
}
