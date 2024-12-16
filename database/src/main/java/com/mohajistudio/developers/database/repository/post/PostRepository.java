package com.mohajistudio.developers.database.repository.post;

import com.mohajistudio.developers.database.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, PostCustomRepository {
}
