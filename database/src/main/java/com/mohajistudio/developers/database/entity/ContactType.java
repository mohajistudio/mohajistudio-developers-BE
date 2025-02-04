package com.mohajistudio.developers.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "contact_types")
public class ContactType extends BaseEntity {
    @Column(length = 20)
    private String name;

    @Column
    private String imageUrl;
}
