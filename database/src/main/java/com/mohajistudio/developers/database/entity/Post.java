package com.mohajistudio.developers.database.entity;

import com.mohajistudio.developers.database.enums.PostStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "posts")
public class Post extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    //TODO 글자수
    @Column(nullable = false)
    private String title;

    //TODO 글자수
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    //TODO 글자수
    @Column(nullable = false)
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String thumbnail;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @Column
    private LocalDateTime publishedAt;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private int viewCount;
}
