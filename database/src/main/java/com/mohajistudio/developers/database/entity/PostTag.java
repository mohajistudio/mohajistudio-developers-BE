package com.mohajistudio.developers.database.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "post_tags", indexes = {
        @Index(name = "idx_post_tag_post_id", columnList = "post_id"),
        @Index(name = "idx_post_tag_tag_id", columnList = "tag_id"),
        @Index(name = "idx_post_tag_user_id", columnList = "user_id"),
})
public class PostTag {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID postId;

    @Column(nullable = false)
    private UUID tagId;

    @Column(nullable = false)
    private UUID userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UuidCreator.getTimeOrdered();
        }
    }
}
