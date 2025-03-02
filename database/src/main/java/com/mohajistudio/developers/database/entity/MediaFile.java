package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.ContentType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "media_files", indexes = {
        @Index(name = "idx_media_file_user_id", columnList = "user_id"),
})
public class MediaFile extends BaseEntity {
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(nullable = false)
    private long size; // byte
}
