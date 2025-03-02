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
@Table(name = "tags", indexes = {
        @Index(name = "idx_tag_title", columnList = "title", unique = true),
})
public class Tag extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(columnDefinition = "INTEGER DEFAULT 0 NOT NULL")
    private int tagCount;
}
