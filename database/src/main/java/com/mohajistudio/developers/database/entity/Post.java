package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.PostStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts", indexes = {
        @Index(name = "idx_post_user_id", columnList = "user_id"),
        @Index(name = "idx_post_status", columnList = "status"),
})
public class Post extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    @Size(min = 1, max = 100)
    private String title;

    //TODO 글자수
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    @Size(max = 200)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column
    private UUID thumbnailId;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column
    private LocalDateTime publishedAt;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private int viewCount;
}
